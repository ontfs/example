<template>
  <div class="register_layout account_wrap">
    <h1>
      <img src="../assets/ontfsb.svg" alt="" />
    </h1>
    <el-card class="account_box-card">
      <div class="tab_change_account">
        <span @click="$router.push({ name: 'LoginWrap' })">Sign In</span>
        /
        <span class="active">Sign Up</span>
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
            >Sign Up</el-button
          >
        </div>
      </div>
      <div class="link" @click="openPage(github)">Visit us on GitHub</div>
    </el-card>
  </div>
</template>
<script>
import { Register } from '@/api/role'
export default {
  data() {
    return {
      github: 'https://github.com/ontfs/example',
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
      }
    }
  },
  methods: {
    submitForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          this.toRegister()
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    async toRegister() {
      try {
        let params = { ...this.ruleForm }
        params.password = this.$utils.default.Encrypt(params.password)
        let apires = await Register(params)
        // console.log(apires)
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
.register_layout {
  position: relative;
  h1 {
    position: absolute;
    left: 100px;
    top: 60px;
    width: 146px;
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
