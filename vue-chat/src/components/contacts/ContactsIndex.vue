<template style="height: 100%">
  <body>
    <el-row>
      <el-col :span="3">
        <div class="grid-content" style="background-color:rgb(219, 219, 219)">
          <!-- Navigation Menu -->
          <nav-menu></nav-menu>
        </div>
      </el-col>
      <el-col :span="21">
        <div>
          <el-row id="rowSearch">
            <!-- Add Contacts -->
            <h5>Add Contacts:</h5>
            <el-input v-model="username" size="small" placeholder="Search by username" prefix-icon="el-icon-search" clearable></el-input>
            <el-button id="btnSearch" size="small" round v-on:click="addFriend">Add</el-button>
          </el-row>
          <el-row id="rowContact" v-if="true">
            <!-- Contacts -->
            <el-scrollbar wrap-class="list" :native="false">
              <single-contact ref="singleContact" ></single-contact>
            </el-scrollbar>
          </el-row>
        </div>
      </el-col>
    </el-row>
  </body>

</template>

<script>
  import NavMenu from '../common/NavMenu'
  import SingleContact from './SingleContact'

  export default {
    name: 'ContactsIndex',
    components: {NavMenu,SingleContact},
    data () {
      return {
        timer: null,
        username: '',
        responseResult: []
      }
    },
//   mounted() {
//     this.timer = setInterval(this.loadContacts(), 1000)
//     this.forceRerender()
//     this.$nextTick(function () {
//           this.loadContacts()
//           console.log("changed")
//     })
//   },
//   beforeDestroy() {
//    clearInterval(this.timer)
//    this.timer = null
//  },
    methods: {
      // loadContacts () {
      //   this.$refs.singleContact.loadContacts()
      // },
      addFriend () {
        var _this = this
        console.log(localStorage.getItem('username'))
        this.$axios
          .post('/addfriend', {
            searchUsername: this.username,
            ownerUsername: localStorage.getItem('username')
          }).then(resp => {
            if (resp.data.code == 200) {
              this.$message({
              type: 'success',
              message: 'Contact Added.'
            })
          }}).catch(failResponse => {
            console.log(failResponse)
            this.$notify.error({
              title: 'Error',
              message: 'Please try again later.'
            });
          })
      }
    }
  }
</script>

<style>
  @import "../../assets/css/home.css";
  @import "../../assets/css/contacts.css";
</style>

