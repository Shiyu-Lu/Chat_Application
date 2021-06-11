<template>
  <body id="bgImg">
    <el-form>
      <el-form-item>
        <h1>Atoms</h1>
        <!-- <h3>A Real-time Web-based Application</h3> -->
        <h3>Let's Stay Connected</h3>
      </el-form-item>
      <el-form-item class="loginContainer">
        <el-form-item>
          <el-input type="text" v-model="loginForm.username" auto-complete="off" placeholder="Username"></el-input>
        </el-form-item>
        <el-form-item>
          <el-input type="password" style="margin-top: 10px" v-model="loginForm.password" auto-complete="off" placeholder="Password" show-password></el-input>
        </el-form-item>
        <el-row class="btnWrapper" style="margin-top: 20px; margin-bottom: 10px">
        <el-button type="primary" style="background: #505458;border: none" v-on:click="signIn">Sign in</el-button>
        <el-button type="primary" style="background: #505458;border: none" v-on:click="signUp">Sign up</el-button>
        </el-row>
      </el-form-item>
    </el-form>
    <footer>Created by Ziyang Huang & Shiyu Lu</footer>
  </body>
</template>

<style scoped>
  @import "../assets/css/styles.css";

  .loginContainer {
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

  .btnwrapper {
    margin-top: 20px;
    padding: 35px 35px 15px 35px;
  }
</style>

<script>
  export default {
    name: 'Login',
    data () {
      return {
        loginForm: {
          username: '',
          password: ''
        },
        responseResult: []
      }
    },
    methods: {
      signIn () {
        var _this = this
        this.$axios
          .post('/login', {
            username: this.loginForm.username,
            password: this.loginForm.password
          })
          .then(successResponse => {
            console.log(successResponse);
            console.log(successResponse.data);
            console.log(successResponse.data.code);
            if (successResponse.data.code == 200) {
              this.$notify({
                title: 'Success',
                message: 'Signed in successfully.',
                type: 'success'
              });

              _this.$store.commit('login', _this.loginForm)
              localStorage.setItem('username', this.loginForm.username)

              var path = this.$route.query.redirect
              this.$router.replace({path: path === '/' || path === undefined ? '/index' : path})
            } else {
              this.$notify.error({
              title: 'Error',
              message: 'Incorrect username or password.'
            });
            }
          })
          .catch(failResponse => {
            console.log(failResponse)
            console.log('Error')
            this.$notify.error({
              title: 'Error',
              message: 'Please try again later.'
            });
          })
      },
      signUp () {
        this.$router.push({path: '/signup'})
      }
    }
  }
</script>
