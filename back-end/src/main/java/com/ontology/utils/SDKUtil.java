package com.ontology.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.Base64;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.*;
import com.github.ontio.common.Helper;
import com.github.ontio.core.asset.Sig;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.crypto.Curve;
import com.github.ontio.crypto.ECC;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.sdk.wallet.Control;
import com.github.ontio.sdk.wallet.Identity;
import com.github.ontio.smartcontract.nativevm.abi.NativeBuildParams;
import com.github.ontio.smartcontract.nativevm.abi.Struct;
import com.ontology.secure.ECIES;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * SDK 入口类
 *
 * @author 12146
 */
@Component
@Slf4j
public class SDKUtil {

    @Autowired
    ConfigParam param;

    public Map<String, String> createOntId(String pwd) throws Exception {
        OntSdk ontSdk = getOntSdk();
        Account payerAcct = new Account(Account.getPrivateKeyFromWIF(param.PAYER_WIF), ontSdk.getWalletMgr().getSignatureScheme());
        HashMap<String, String> res = new HashMap<>();
        Identity identity = ontSdk.getWalletMgr().createIdentity(pwd);
        String txhash = ontSdk.nativevm().ontId().sendRegister(identity, pwd, payerAcct, 20000, 500);
        ontSdk.getWalletMgr().getWallet().clearIdentity();
        ontSdk.getWalletMgr().writeWallet();
        Map keystore = WalletQR.exportIdentityQRCode(ontSdk.getWalletMgr().getWallet(), identity);
        keystore.put("publicKey", identity.controls.get(0).publicKey);
        res.put("ontid", identity.ontid);
        res.put("keystore", JSON.toJSONString(keystore));
        res.put("tx", txhash);
        ontSdk.getWalletMgr().getWallet().clearIdentity();
        return res;
    }

    public String createOntIdWithWif(String wif, String pwd) throws Exception {
        OntSdk ontSdk = getOntSdk();
        byte[] bytes = com.github.ontio.account.Account.getPrivateKeyFromWIF(wif);
        Identity identity = ontSdk.getWalletMgr().createIdentityFromPriKey(pwd, Helper.toHexString(bytes));
        Map keystore = WalletQR.exportIdentityQRCode(ontSdk.getWalletMgr().getWallet(), identity);
        ontSdk.getWalletMgr().getWallet().clearIdentity();
        return JSON.toJSONString(keystore);
    }

    public String checkOntId(String keystore, String pwd) throws Exception {
        Account account = exportAccount(keystore, pwd);
        return Common.didont + Address.addressFromPubKey(account.serializePublicKey()).toBase58();
    }

    private Account exportAccount(String keystoreBefore, String pwd) throws Exception {
        OntSdk ontSdk = getOntSdk();
        String keystore = keystoreBefore.replace("\\", "");
        JSONObject jsonObject = JSON.parseObject(keystore);
        String key = jsonObject.getString("key");
        String address = jsonObject.getString("address");
        String saltStr = jsonObject.getString("salt");

        int scrypt = jsonObject.getJSONObject("scrypt").getIntValue("n");
        String privateKey = Account.getGcmDecodedPrivateKey(key, pwd, address, Base64.decodeFast(saltStr), scrypt, ontSdk.getWalletMgr().getSignatureScheme());
        return new Account(Helper.hexToBytes(privateKey), ontSdk.getWalletMgr().getSignatureScheme());
    }

    public String exportWif(String keystore, String pwd) throws Exception {
        Account account = exportAccount(keystore, pwd);
        return account.exportWif();
    }

    public String decryptData(String keystore, String pwd, String[] data) throws Exception {
        Account account = exportAccount(keystore, pwd);
        byte[] decrypt = ECIES.Decrypt(account, data);
        return new String(decrypt);
    }


    private OntSdk wm;

    private OntSdk getOntSdk() {
        if (wm == null) {
            wm = OntSdk.getInstance();
            wm.setRestful(param.RESTFUL_URL);
            wm.openWalletFile("wallet.json");
        }
        if (wm.getWalletMgr() == null) {
            wm.openWalletFile("wallet.json");
        }
        return wm;
    }

    public String checkOntIdDDO(String ontidStr) throws Exception {
        return getOntSdk().nativevm().ontId().sendGetDDO(ontidStr);
    }

    public Account getAccount(String keystore, String pwd) throws Exception {
        Account account = exportAccount(keystore, pwd);
        return account;
    }

    public String invokeContract(byte[] params, Account Acct, Account payerAcct, long gaslimit, long gasprice, boolean preExec) throws Exception {
        OntSdk ontSdk = getOntSdk();
        if (payerAcct == null) {
            throw new SDKException("params should not be null");
        }
        if (gaslimit < 0 || gasprice < 0) {
            throw new SDKException("gaslimit or gasprice should not be less than 0");
        }

        Transaction tx = ontSdk.vm().makeInvokeCodeTransaction(Helper.reverse("04e1d2914999b485896f6c42b729565f8f92d625"), "send_token", params, payerAcct.getAddressU160().toBase58(), gaslimit, gasprice);

        ontSdk.addSign(tx, payerAcct);
        ontSdk.addSign(tx, Acct);

        Object result = null;
        if (preExec) {
            result = ontSdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
        } else {
            result = ontSdk.getConnect().sendRawTransaction(tx.toHexString());
        }
        return tx.hash().toString();
    }

    public Object invokeContract(String str, Account acct, Account payerAcct, boolean preExec) throws Exception {
        OntSdk ontSdk = getOntSdk();
        Transaction[] txs1 = ontSdk.makeTransactionByJson(str);
        Object result = null;
        if (preExec) {
            result = ontSdk.getConnect().sendRawTransactionPreExec(txs1[0].toHexString());
            return result;
        } else {
            ontSdk.addSign(txs1[0], acct);
            ontSdk.addSign(txs1[0], payerAcct);
            result = ontSdk.getConnect().sendRawTransaction(txs1[0].toHexString());
        }
        return txs1[0].hash().toString();
    }

    public Object invokeContract(String params, String wif, boolean preExec) throws Exception {
        OntSdk ontSdk = getOntSdk();
        Transaction[] txs1 = ontSdk.makeTransactionByJson(params);
        byte[] bytes = Account.getPrivateKeyFromWIF(wif);
        Account account = new Account(bytes, ontSdk.getWalletMgr().getSignatureScheme());
        ontSdk.addSign(txs1[0], account);
        Object result = null;
        if (preExec) {
            result = ontSdk.getConnect().sendRawTransactionPreExec(txs1[0].toHexString());
            return result;
        } else {
            result = ontSdk.getConnect().sendRawTransaction(txs1[0].toHexString());
        }
        return txs1[0].hash().toString();
    }

    public Object checkEvent(String txHash) throws Exception {
        OntSdk ontSdk = getOntSdk();
        Object event = ontSdk.getConnect().getSmartCodeEvent(txHash);
        return event;
    }

    public void queryBlance(String address) throws Exception {
        OntSdk ontSdk = getOntSdk();
//        String s = ontSdk.neovm().oep4().queryBalanceOf("AKRwxnCzPgBHRKczVxranWimQBFBsVkb1y");
        long ontBalance = ontSdk.nativevm().ont().queryBalanceOf(address);
        long ongBalance = ontSdk.nativevm().ong().queryBalanceOf(address);
        System.out.println("ont:" + ontBalance);
        System.out.println("ong:" + ongBalance);
    }

    public int getBlockHeight() throws Exception {
        OntSdk ontSdk = getOntSdk();
        int blockHeight = ontSdk.getConnect().getBlockHeight();
        return blockHeight;
    }

    public Object getSmartCodeEvent(int height) throws Exception {
        OntSdk ontSdk = getOntSdk();
        Object smartCodeEvent = ontSdk.getConnect().getSmartCodeEvent(height);
        return smartCodeEvent;
    }

    public int HeightBytx(String s) throws Exception {
        OntSdk ontSdk = getOntSdk();
        int smartCodeEvent = ontSdk.getConnect().getBlockHeightByTxHash(s);
        return smartCodeEvent;

    }

    public Object makeTransaction(String params) throws Exception {
        OntSdk ontSdk = getOntSdk();
        Transaction[] txs1 = ontSdk.makeTransactionByJson(params);
        return txs1[0];
    }

    public String sendTransaction(String txHex, String pubKey, String signature) throws Exception {
        byte[] bytes = Helper.hexToBytes(txHex);
        Transaction tx = Transaction.deserializeFrom(bytes);
        log.info("payer:{}", tx.payer.toBase58());
        Sig[] sigs = new Sig[1];
        sigs[0] = new Sig();
        sigs[0].M = 1;
        sigs[0].pubKeys = new byte[1][];
        sigs[0].sigData = new byte[1][];
        sigs[0].pubKeys[0] = Helper.hexToBytes(pubKey);
        sigs[0].sigData[0] = Helper.hexToBytes(signature);
        tx.sigs = sigs;

        OntSdk ontSdk = getOntSdk();
        Account payer = new Account(Account.getPrivateKeyFromWIF(param.PAYER_WIF), ontSdk.getWalletMgr().getSignatureScheme());
        ontSdk.addSign(tx, payer);
        ontSdk.getConnect().sendRawTransaction(tx);
        return tx.hash().toString();
    }

    public Object sendTransaction(Transaction transaction, String acctWif, boolean preExec) throws Exception {
        OntSdk ontSdk = getOntSdk();
        log.info("ontSdk:{}", ontSdk);
        log.info("acctWif:{}", acctWif);
        log.info("wif:{}", Account.getPrivateKeyFromWIF(acctWif));
        log.info("getWalletMgr:{}", ontSdk.getWalletMgr());
        log.info("scheme:{}", ontSdk.getWalletMgr().getSignatureScheme());
        Account account = new Account(Account.getPrivateKeyFromWIF(acctWif), ontSdk.getWalletMgr().getSignatureScheme());
        ontSdk.addSign(transaction, account);
        if (preExec) {
            return ontSdk.getConnect().sendRawTransactionPreExec(transaction.toHexString());
        } else {
            ontSdk.getConnect().sendRawTransaction(transaction.toHexString());
            return transaction.hash().toString();
        }
    }

    public Map<String, Object> makeRegIdWithController(String ontid) throws Exception {
        OntSdk ontSdk = getOntSdk();
        String dataId = ontSdk.getWalletMgr().createIdentity("").ontid;

        byte[] arg;
        List<Object> list = new ArrayList<>();
        list.add(new Struct().add(dataId, ontid, 1));
        arg = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = ontSdk.vm().buildNativeParams(new Address(Helper.hexToBytes("0000000000000000000000000000000000000003")), "regIDWithController", arg, param.PAYER_ADDRESS, Constant.GAS_LIMIT, Constant.GAS_PRICE);

        Map<String, Object> map = new HashMap<>();
        map.put("dataId", dataId);
        map.put("tx", tx);
        return map;
    }

    public String createDataId() throws Exception {
        OntSdk ontSdk = getOntSdk();
        return createIdentity(ontSdk, "").ontid;
    }

    private Identity createIdentity(OntSdk ontSdk, String password) throws Exception {
        String label = "";
        byte[] prikey = ECC.generateKey();
        byte[] salt = ECC.generateKey(16);
        com.github.ontio.account.Account account = new com.github.ontio.account.Account(prikey, ontSdk.getWalletMgr().getSignatureScheme());
        com.github.ontio.sdk.wallet.Account acct;
        switch (ontSdk.getWalletMgr().getSignatureScheme()) {
            case SHA256WITHECDSA:
                acct = new com.github.ontio.sdk.wallet.Account("ECDSA", new Object[]{Curve.P256.toString()}, "aes-256-gcm", "SHA256withECDSA", "sha256");
                break;
            case SM3WITHSM2:
                acct = new com.github.ontio.sdk.wallet.Account("SM2", new Object[]{Curve.SM2P256V1.toString()}, "aes-256-gcm", "SM3withSM2", "sha256");
                break;
            default:
                throw new SDKException(ErrorCode.OtherError("scheme type error"));
        }
        if (password != null) {
            acct.key = account.exportGcmEncryptedPrikey(password, salt, ontSdk.getWalletMgr().getWalletFile().getScrypt().getN());
            password = null;
        } else {
            acct.key = Helper.toHexString(account.serializePrivateKey());
        }
        acct.address = Address.addressFromPubKey(account.serializePublicKey()).toBase58();
        if (label == null || label.equals("")) {
            String uuidStr = UUID.randomUUID().toString();
            label = uuidStr.substring(0, 8);
        }

        Identity idt = new Identity();
        idt.ontid = Common.didont + acct.address;
        idt.label = label;

        idt.controls = new ArrayList<Control>();
        Control ctl = new Control(acct.key, "keys-1", Helper.toHexString(account.serializePublicKey()));
        ctl.setSalt(salt);
        ctl.setAddress(acct.address);
        idt.controls.add(ctl);
        return idt;
    }

    public Object sendPreTransaction(String params) throws Exception {
        OntSdk ontSdk = getOntSdk();
        Transaction[] txs = ontSdk.makeTransactionByJson(params);
        Object o = ontSdk.getConnect().sendRawTransactionPreExec(txs[0].toHexString());
        return o;
    }

    public boolean verifyMessage(String pubKey, String message, String signature) throws Exception {
        OntSdk ontSdk = getOntSdk();
        byte[] pubKeyBytes = Helper.hexToBytes(pubKey);
        byte[] signatureBytes = Helper.hexToBytes(signature);
        boolean b = ontSdk.verifySignature(pubKeyBytes, message.getBytes(), signatureBytes);
        return b;
    }

    public boolean verifyTransaction(String pubKey, String message, String signature) throws Exception {
        OntSdk ontSdk = getOntSdk();
        byte[] pubKeyBytes = Helper.hexToBytes(pubKey);
        byte[] dataBytes = Helper.hexToBytes(message);
        byte[] signatureBytes = Helper.hexToBytes(signature);
        boolean b = ontSdk.verifySignature(pubKeyBytes, dataBytes, signatureBytes);
        return b;
    }

    public String getPublicKey(String ontid) throws Exception {
        String ddo = checkOntIdDDO(ontid);
        Map map = (Map) JSON.parseObject(ddo).getJSONArray("Owners").get(0);
        return (String) map.get("Value");
    }

    public String transferOng(String sendAddress, String receiveAddress, long amount, String wif) throws Exception {
        OntSdk ontSdk = getOntSdk();
        Transaction transaction = ontSdk.nativevm().ong().makeTransfer(sendAddress, receiveAddress, amount, sendAddress, 20000L, 500L);
        Account payerAcct = new Account(Account.getPrivateKeyFromWIF(wif), ontSdk.getWalletMgr().getSignatureScheme());
        ontSdk.addSign(transaction,payerAcct);
        ontSdk.getConnect().sendRawTransaction(transaction);
        return transaction.hash().toString();
    }

    public long checkOngBalance(String address) throws Exception {
        OntSdk ontSdk = getOntSdk();
        return ontSdk.nativevm().ong().queryBalanceOf(address);
    }
}
