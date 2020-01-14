<template>
  <div class="home">
    <el-card class="home_card accout_header">
      <h1>
        <img src="../assets/ontfsb.svg" alt="" />
      </h1>
      <span class="account_name">Account: {{ userName }}</span>
      <span class="account_name">ONT ID: {{ ontId }}</span>
      <el-button type="text" @click="logOut">Sign out</el-button>
    </el-card>
    <el-card class="home_card">
      <div class="file_wrap">
        <el-form ref="form" :rules="rules" :model="form" label-width="180px">
          <el-form-item label="File encryption code" prop="password">
            <el-input
              autocomplete="off"
              type="password"
              v-model="form.password"
              placeholder="Create an encryption code"
            ></el-input>
          </el-form-item>
          <el-form-item label="File">
            <el-upload
              ref="upload"
              class="upload-demo"
              action
              :on-change="handleChange"
              :on-remove="handleRemove"
              :file-list="fileList"
              :http-request="fileupload"
              :limit="1"
              :before-upload="beforeAvatarUpload"
            >
              <el-button size="small" type="primary">Select</el-button>
              <div slot="tip" class="el-upload__tip">
                Good news! You can upload files up to 2 MB at a time.
              </div>
            </el-upload>
          </el-form-item>
        </el-form>
        <el-button @click="submitForm('form')" size="small" type="primary"
          >Upload</el-button
        >
      </div>
    </el-card>
    <el-card class="home_card">
      <el-table
        empty-text="No Data"
        :data="tableData"
        border
        style="width: 100%"
      >
        <el-table-column prop="fileName" label="File Name" width="220">
        </el-table-column>
        <el-table-column prop="fileHash" label="File Hash"> </el-table-column>
        <el-table-column
          prop="txHash"
          label="Transation Hash"
          align="center"
          width="240"
        >
          <template slot-scope="scope">
            <a
              class="s-color"
              target="_blank"
              :href="domain + '/transaction/' + scope.row.txHash"
              >{{
                scope.row.txHash.substr(0, 8) +
                  '...' +
                  scope.row.txHash.substr(56, 8)
              }}</a
            >
          </template>
        </el-table-column>
        <el-table-column
          prop="createTime"
          align="center"
          label="Upload Date"
          width="220"
        >
          <template slot-scope="scope">
            {{ $utils.default.HelperTools.getTransDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="Action" align="center" width="140">
          <template slot-scope="scope">
            <el-button
              @click="openDownload(scope.row.fileHash)"
              type="text"
              size="small"
              >Download</el-button
            >
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <!-- <el-button @click="open">Click</el-button> -->
    <el-card
      ><div style="text-align: center; font-size: 14px;">
        This is an online software demo powered by Ontology.
      </div></el-card
    >
  </div>
</template>

<script>
import axios from 'axios'
import { uploadfile } from '@/api/file'
import { getFileList, downloadFile } from '@/api/role'

export default {
  data() {
    return {
      userName: '',
      ontId: '',
      fileList: [],
      localFile: {},
      form: {
        password: ''
      },
      rules: {
        password: [
          {
            required: true,
            message: 'Please enter file encryption code',
            trigger: 'blur'
          },
          {
            pattern: /^[\S]{6,12}$/,
            min: 6,
            max: 12,
            message: '6~12 characters in length and cannot be empty!',
            trigger: 'change'
          }
        ]
      },
      fileObject: null,
      tableData: [
        // {
        //   fileName: '2016-05-02',
        //   createTime: '王小虎',
        //   fileHash: '上海市普陀区金沙江路 1518 弄'
        // },
        // {
        //   fileName: '2016-05-04',
        //   createTime: '王小虎',
        //   fileHash: '上海市普陀区金沙江路 1517 弄'
        // },
        // {
        //   fileName: '2016-05-01',
        //   createTime: '王小虎',
        //   fileHash: '上海市普陀区金沙江路 1519 弄'
        // },
        // {
        //   fileName: '2016-05-03',
        //   createTime: '王小虎',
        //   fileHash: '上海市普陀区金沙江路 1516 弄'
        // }
      ],
      domain: process.env.VUE_APP_DOMAIN
    }
  },
  methods: {
    logOut() {
      sessionStorage.clear()
      this.$router.push({ path: '/login' })
    },
    test() {
      const res = this.$utils.default.Encrypt('testpwd')
      console.log(res)
      const aaaa = this.$utils.default.Decrypt(res)
      console.log(aaaa)
    },
    handleRemove(file, fileList) {
      this.fileObject = null
    },
    handlePreview(file) {
      console.log(file)
    },
    handleChange(file, fileList) {
      console.log(file)
      // this.localFile = file.raw
      // let reader = new FileReader()
      // reader.readAsDataURL(this.localFile) // 这里也可以直接写参数event.raw

      // 转换成功后的操作，reader.result即为转换后的DataURL ，
      // 它不需要自己定义，你可以console.log(reader.result)看一下
      // reader.onload = () => {
      //   console.log(reader.result)
      // }
    },
    fileupload(params) {
      this.fileObject = params.file
    },
    beforeAvatarUpload(file) {
      const isLt2M = file.size / 1024 / 1024 < 2
      if (!isLt2M) {
        this.$message.error('Only files within 2 MB are allowed')
      }
      return isLt2M
    },
    submitForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          if (!this.fileObject) {
            return this.$message.error('Please select a file!')
          }
          this.handlerUpload()
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    async handlerUpload() {
      let formData = new FormData()
      formData.append('file', this.fileObject)
      formData.append('ontId', this.ontId)
      formData.append(
        'password',
        this.$utils.default.Encrypt(this.form.password)
      )

      let data = formData
      const loading = this.$loading({
        lock: true,
        text: 'Loading',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.2)'
      })
      let resapi = await axios.post(
        process.env.VUE_APP_API + '/api/v1/data/upload',
        data,
        {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        }
      )
      // console.log(resapi)
      const { desc, error, result } = resapi.data
      if (desc === 'SUCCESS' && error === 0) {
        loading.close()
        this.open()
        this.resetForm('form')
        this.$refs.upload.clearFiles()
        this.fileObject = null
        this.handlerGetFile()
      } else {
        loading.close()
        this.$message({
          type: 'error',
          message: 'upload fail, please try again!'
        })
      }
    },
    open() {
      this.$alert(
        'File uploaded successfully! It will be available for access in approximately 2 minutes.',
        'Tips',
        {
          confirmButtonText: 'Confirm',
          callback: action => {}
        }
      )
    },
    resetForm(formName) {
      this.$refs[formName].resetFields()
    },
    async handlerGetFile() {
      try {
        let apires = await getFileList(this.ontId)
        const { desc, error, result } = apires
        if (desc === 'SUCCESS' && error === 0) {
          this.tableData = [...result]
        }
      } catch (error) {
        this.tableData = []
      }
    },
    openDownload(fileHash) {
      this.$prompt('Please enter file decryption code', 'Tips', {
        confirmButtonText: 'Confirm',
        cancelButtonText: 'Cancel',
        inputPattern: /^[\S]{6,12}$/,
        inputType: 'password',
        inputErrorMessage: '6~12 characters in length and cannot be empty!'
      })
        .then(({ value }) => {
          // console.log(value)
          this.handlerDownloadFile(fileHash, value)
        })
        .catch(() => {})
    },
    async handlerDownloadFile(fileHash, password) {
      let params = {
        fileHash,
        password: this.$utils.default.Encrypt(password)
      }
      const loading = this.$loading({
        lock: true,
        text: 'Loading',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.2)'
      })
      try {
        let apires = await downloadFile(params)
        const { error, desc, result } = apires
        if (error === 0 && desc === 'SUCCESS') {
          loading.close()
          window.open(result, '_self')
        } else {
          let str = ''
          if (
            desc ==
            'no available fs nodes to download file: no node had commit pdp prove data'
          ) {
            str =
              'Uploading file to ONTFS Test Net right now. Please try again later!'
          } else if (desc == 'wrong password') {
            str = 'Wrong decryption code. Please try again.'
          } else {
            str = 'Error. Please try again!'
          }
          this.$message({
            type: 'error',
            message: str
          })
          loading.close()
        }
      } catch (error) {
        loading.close()
      }
    }
  },
  mounted() {
    this.userName = sessionStorage.getItem('userName')
    this.ontId = sessionStorage.getItem('ontId')
    this.handlerGetFile()
  }
}
</script>

<style lang="less" scoped>
.home {
  max-width: 1440px;
  margin: 0 auto;
  width: 100%;
  height: 100%;
  padding: 20px;
  .home_card {
    margin-bottom: 20px;
  }
  .accout_header {
    text-align: right;
    .account_name {
      margin-right: 50px;
    }
  }

  .file_wrap {
    width: 100%;
    max-width: 400px;
    .upload-demo {
      height: 150px;
    }
  }
}
/deep/.el-card__body {
  position: relative;
  h1 {
    position: absolute;
    left: 20px;
    top: 50%;
    transform: translateY(-50%);
    img {
      width: 110px;
    }
  }
}
a.s-color {
  color: #409eff;
  cursor: pointer;
}
/deep/.el-upload {
  transform: translateX(150px;);
}
</style>
