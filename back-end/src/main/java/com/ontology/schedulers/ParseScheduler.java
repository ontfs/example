//package com.ontology.schedulers;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.github.ontio.common.Helper;
//import com.ontology.entity.TxResult;
//import com.ontology.mapper.TxResultMapper;
//import com.ontology.utils.ConfigParam;
//import com.ontology.utils.Constant;
//import com.ontology.utils.SDKUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//
//import java.util.ArrayList;
//import java.util.List;
//
//
//@Component
//@Slf4j
//@EnableScheduling
//public class ParseScheduler extends BaseScheduler {
//
//    @Autowired
//    private SDKUtil sdk;
//    @Autowired
//    private ConfigParam configParam;
//    @Autowired
//    private TxResultMapper txResultMapper;
//
//    private static Integer height;
//
//    /**
//     * 同步链上信息
//     */
//    @Scheduled(initialDelay = 5000, fixedDelay = 6000)
//    public void synchronizeData() {
//        log.info("synchronizeData schedule : {}", Thread.currentThread().getName());
//        int i = 0;
//        try {
//            int blockHeight = sdk.getBlockHeight();
//            int currentHeight;
//
//            if (height == null) {
//                height = blockHeight;
//                currentHeight = blockHeight;
//            } else {
//                currentHeight = height;
//            }
//
//            log.info("最大块高：{}", blockHeight);
//            log.info("开始块高：{}", currentHeight);
//
//            for (i = currentHeight; i <= blockHeight; i++) {
//                log.info("当前块高:{}", i);
//                Object events = sdk.getSmartCodeEvent(i);
//                log.info("events:{}", events);
//                if (!StringUtils.isEmpty(events)) {
//                    JSONArray eventList = (JSONArray) events;
//                    for (int j = 0; j < eventList.size(); j++) {
//                        JSONObject event = eventList.getJSONObject(j);
//                        JSONArray notifys = event.getJSONArray("Notify");
//                        String txHash = event.getString("TxHash");
//                        Integer state = event.getInteger("State");
//
//                        TxResult record = new TxResult();
//                        record.setTxHash(txHash);
//                        TxResult txResult = txResultMapper.selectOne(record);
//                        if (txResult != null) {
//                            txResult.setTxOnchainState(state);
//                        }
//
//                        for (int k = 0; k < notifys.size(); k++) {
//                            JSONObject notify = notifys.getJSONObject(k);
//                            if (configParam.CONTRACT_HASH_MP_AUTH.equals(notify.getString("ContractAddress"))) {
//                                // 智能合约地址匹配，解析结果
//                                Object statesObj = notify.get("States");
//
//                                JSONArray states = (JSONArray) statesObj;
//                                String method = new String(Helper.hexToBytes(states.getString(0)));
//                                log.info(method);
//
//                                if ("authOrder".equals(method)) {
//                                    // makeOrder推送的事件
//                                    String authId = states.getString(2);
//                                    String dataId = new String(Helper.hexToBytes(states.getString(6)), "utf-8");
//                                    log.info("authId:{}", authId);
//                                    log.info("dataId:{}", dataId);
//
//                                    // 保存authId
//                                    if (txResult != null) {
//                                        txResult.setBusinessId(authId);
//                                    }
//
//                                } else if ("takeOrder".equals(method)) {
//                                    // takeOrder推送的事件，同时解析mintToken
//                                    List<Long> tokenId = new ArrayList<>();
//                                    for (int m = 0; m < notifys.size(); m++) {
//                                        JSONObject dTokenNotify = notifys.getJSONObject(m);
//                                        if (configParam.CONTRACT_HASH_DTOKEN.equals(dTokenNotify.getString("ContractAddress"))) {
//                                            JSONArray dTokenStates = (JSONArray) dTokenNotify.get("States");
//                                            String dTokenMethod = new String(Helper.hexToBytes(dTokenStates.getString(0)));
//                                            log.info(dTokenMethod);
//                                            if ("mintToken".equals(dTokenMethod)) {
//                                                long startTokenId = (Long.parseLong(Helper.reverse(dTokenStates.getString(6)), 16));
//                                                long endTokenId = (Long.parseLong(Helper.reverse(dTokenStates.getString(8)), 16));
//                                                long amount = (Long.parseLong(Helper.reverse(dTokenStates.getString(14)), 16));
//                                                tokenId.add(startTokenId);
//                                                tokenId.add(endTokenId);
//                                                break;
//                                            }
//                                        }
//                                    }
//                                    String orderId = states.getString(2);
//
//                                    // 保存orderId
//                                    if (txResult != null) {
//                                        txResult.setBusinessId(orderId + Constant.SPLIT + JSON.toJSONString(tokenId));
//                                    }
//                                }
//                            }
//                        }
//                        if (txResult != null) {
//                            txResultMapper.updateByPrimaryKeySelective(txResult);
//                        }
//                    }
//                }
//            }
//            height = i;
//        } catch (Exception e) {
//            log.error("catch exception:", e);
//            if (i != 0) {
//                height = i;
//            }
//        }
//    }
//
//}
