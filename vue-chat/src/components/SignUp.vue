<template>
  <body id="bgImg">
    <el-page-header @back="goback" title="Back" id="btnBack"></el-page-header>
    <el-form :model="signUpForm" status-icon :rules="rules" ref="signUpForm">
      <el-form-item>
        <h2>Sign Up</h2>
      </el-form-item>
      <el-form-item class="signUpContainer">
        <el-form-item prop="username">
          <el-input type="text" v-model="signUpForm.username" auto-complete="off" placeholder="Username"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input type="password" style="margin-top: 25px" v-model="signUpForm.password" auto-complete="off" placeholder="Password"></el-input>
        </el-form-item>
        <el-form-item prop="checkPassword">
          <el-input type="password" style="margin-top: 25px" v-model="signUpForm.checkPassword" auto-complete="off" placeholder="Confirm the Password"></el-input>
        </el-form-item>
        <el-form-item>
        <el-button type="primary" style="margin-top: 25px;background: #505458;border: none" @click="signUp('signUpForm')">Sign up</el-button>
        </el-form-item>
      </el-form-item>
    </el-form>
    <footer>Created by Ziyang Huang & Shiyu Lu</footer>
  </body>
</template>


<style scoped>
  @import "../assets/css/styles.css";

  #btnBack {
    margin-top: 80px;
    margin-left: 80px;
    font-size: 1em !important;
  }

  .signUpContainer {
    border-radius: 15px;
    background-clip: padding-box;
    margin: 20px auto;
    width: 350px;
    padding: 35px 35px 15px 35px;
    background: #fff;
    opacity: 0.99;
    border: 1px solid #eaeaea;
    box-shadow: 0 0 25px #cac6c6;
  }
</style>

<script>

  export default {
    name: 'SignUp',
    data () {
      var checkUserName = (rule, value, callback) => {
        if (value == '') {
          callback(new Error('Please enter a username.'));
        } else {
          callback();
        }
      }
      var validatePass = (rule, value, callback) => {
        if (value == '') {
          callback(new Error('Please enter a password.'));
        } else {
          if (this.signUpForm.checkPassword !== '') {
            this.$refs.signUpForm.validateField('checkPassword');
          }
          callback();
        }
      }
      var validatePass2 = (rule, value, callback) => {
        if (value == '') {
          callback(new Error('Please re-enter your password.'));
        } else if (value != this.signUpForm.password) {
          callback(new Error("Please confirm the password."))
        } else {
          callback();
        }
      };
      return {
        signUpForm: {
          username: '',
          password: '',
          checkPassword: ''
        },
        rules: {
          username: [
            {validator: checkUserName, trigger: 'blur'}
          ],
          password: [
            {validator: validatePass, trigger: 'blur' }
          ],
          checkPassword: [
            {validator: validatePass2, trigger: 'blur' }
          ]
        }
      }
    },
    methods: {
      goback() {
        this.$router.push({path: '/login'});
      },
      signUp (formName) {
        var _this = this
        console.log('validating')
        this.$refs[formName].validate((valid) => {
          if (valid) {
            console.log('Successful submit!')
          } else {
            console.log('Error submit!');
            return false;
          }
          this.$axios
          .post('/signup', {
            username: this.signUpForm.username,
            password: this.signUpForm.password
          })
          .then(successResponse => {
            console.log(successResponse);
            console.log(successResponse.data);
            console.log(successResponse.data.code);
            if (successResponse.data.code == 200) {
              // var data = this.signUpForm
              this.$notify({
                title: 'Success',
                message: 'Signed up successfully.',
                type: 'success'
              });
              this.$router.push({path: '/login'});
            } else if (successResponse.data.code == 300) {
              this.$notify.error({
                title: 'Error',
                message: 'Usename existed. Please choose anothe one.'
              });
            }
          })
          .catch(failResponse => {
            this.$notify.error({
              title: 'Error',
              message: 'Please try again later.'
            });
          })
        });
      }
    }
  }
</script>
