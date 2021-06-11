<template style="height: 100%">
  <el-menu
    :default-active="activeIndex"
    :router="true"
    class="SideBar"
    text-color="rgb(131, 134, 140)"
    active-text-color=#21a675
    unique-opened>
    <el-menu-item index="/index">
      <i class="el-icon-chat-round" style="margin-left:15px"></i>
      <span>Chats</span>
    </el-menu-item>
    <el-menu-item index="/contacts">
      <i class="el-icon-user" style="margin-left:15px"></i>
      <span>Contacts</span>
    </el-menu-item>
    <li style="flex:1;"></li>
    <el-button plain id="logOut" icon="el-icon-switch-button" v-on:click="logOut"></el-button>
  </el-menu>
</template>

<script>
  export default {
    name: 'NavMenu',
    data () {
      return {}
    },

    computed: {
      activeIndex () {
        console.log('/'+this.$route.matched[1].path.split('/')[1]);
        return '/'+this.$route.matched[1].path.split('/')[1]
      }
    },

    methods: {
      logOut () {
        this.$confirm('Sign out your account.', {
          confirmButtonText: 'Sign Out',
          cancelButtonText: 'Cancel',
          type: 'warning'
        }).then(() => {
          var _this = this
          this.$axios.get('/logout').then(resp => {
            if (resp.data.code == 200) {
              // 前后端状态保持一致
              _this.$store.commit('logout')
              this.$message({
                type: 'success',
                message: 'Signed out.'
              }),
              this.$router.push({path: '/login'});
            }
          })
        }).catch(() => {
          this.$message({
            type: 'info',
            message: 'Cancelled to sign out.'
          })
        })
      }
    }
  }
</script>

<style>
  @import "../../assets/css/menu.css";
</style>
