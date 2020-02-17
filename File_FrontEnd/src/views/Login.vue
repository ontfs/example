<template>
  <div class="login_layout account_wrap">
    <h1>
      <img src="../assets/ontfsb.svg" alt="" />
    </h1>
    <el-card class="account_box-card">
      <div class="tab_change_account">
        <span class="active">Sign In</span>
        /
        <span @click="$router.push({ name: 'RegisterWrap' })">Sign Up</span>
      </div>
      <div class="account_form">
        <el-form
          :model="ruleForm"
          :rules="rules"
          ref="ruleForm"
          label-width="140px"
          class="demo-ruleForm"
        >
          <el-form-item label="Account Name" prop="userName">
            <el-input
              placeholder="Please enter account name"
              v-model="ruleForm.userName"
            ></el-input>
          </el-form-item>
          <el-form-item label="Password" prop="password">
            <el-input
              placeholder="Please enter password"
              type="password"
              v-model="ruleForm.password"
            ></el-input>
          </el-form-item>
        </el-form>
        <div class="acc_btn">
          <el-button type="primary" @click="submitForm('ruleForm')"
            >Sign In</el-button
          >
        </div>
      </div>
      <div class="link" @click="openPage(github)">Visit us on GitHub</div>
    </el-card>
  </div>
</template>

<script>
import { Login } from '@/api/role'
export default {
  data() {
    return {
      ruleForm: {
        userName: '',
        password: ''
      },
      rules: {
        userName: [
          {
            required: true,
            message: 'Please enter account name',
            trigger: 'blur'
          },
          {
            pattern: /^[\S]{4,12}$/,
            min: 4,
            max: 12,
            message: '4~12 characters in length and cannot be empty!',
            trigger: 'change'
          }
        ],
        password: [
          {
            required: true,
            message: 'Please enter password',
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
      github: 'https://github.com/ontfs/example'
    }
  },
  methods: {
    submitForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          this.toLogin()
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    async toLogin() {
      let params = { ...this.ruleForm }
      try {
        params.password = this.$utils.default.Encrypt(params.password)
        let apires = await Login(params)
        const { desc, result } = apires
        if (desc === 'SUCCESS') {
          this.$message({
            message: 'Have fun. Stay cool. Change the world.',
            type: 'success',
            center: true,
            duration: 2000
          })
          sessionStorage.setItem('userName', result.userName)
          sessionStorage.setItem('ontId', result.ontId)
          this.$router.push({ path: '/' })
        } else {
          this.$message({
            message: desc,
            type: 'error',
            center: true,
            duration: 2000
          })
        }
      } catch (error) {
        return error
      }
    },
    openPage(url) {
      window.open(url)
    }
  }
}
</script>

<style lang="less" scoped>
.login_layout {
  position: relative;
  h1 {
    position: absolute;
    left: 100px;
    top: 60px;
    width: 146px;
    height: 30px;
  }
}
.link {
  margin-top: 20px;
  font-size: 14px;
  color: #666;
  text-decoration: underline;
  text-align: center;
  cursor: pointer;
  transition: all 0.5s;
  &:hover {
    color: #000;
  }
}
</style>
