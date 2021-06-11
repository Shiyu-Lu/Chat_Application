<template>
  <div>
    <el-row id="card"  v-for="contact in contacts" :key="contact.index" v-bind:content="contact" >
      <div id="avatar" ><el-avatar icon="el-icon-user-solid"></el-avatar></div>
      <h5 id='name'>{{contact.convUserName}}</h5>
      <el-button id='btnDel' size="medium" type="danger" icon="el-icon-delete" v-on:click="delContact(contact.convUserName)"></el-button>
    </el-row>
  </div>
</template>

<script>
export default({
  name: 'SingleContact',
  data() {
    return {
      timer: 1000,
      contacts: []
    }
  },
  created() {
    window.setInterval(() => this.loadContacts(),this.timer);
  },
  mounted() {
    this.loadContacts()
    // setInterval(this.loadContacts(), 1000)
    // this.$nextTick(function () {
    //       this.loadContacts()
    // })
  },
  beforeRouteLeave (to, from, next) {
      next()
      clearInterval(this.timer)
      this.timer = undefined
  },
  destroyed() {
    window.clearInterval(this.timer)
    // this.timer = null
  },
  methods: {
    loadContacts () {
        var _this = this
        console.log('loading')
        // console.log(localStorage.getItem('username'))

        this.$axios.post('/msglist',{
          ownerUsername: localStorage.getItem('username')
        }).then(resp => {
          // console.log(resp)
          // console.log(resp.data)
          // if (resp.data.code === 200) {
          // console.log('success')

          var json = JSON.stringify(resp.data);
          _this.contacts = JSON.parse(json);
          // console.log(json);
          // console.log(_this.contacts[0].convUserName);

          _this.$forceUpdate()
          this.$forceUpdate()
          // }
        }).catch(failResponse => {
          console.log(failResponse)
        })
      },
    delContact: function(name) {
      this.$confirm('Delete contact and all associated chats', {
          confirmButtonText: 'Delete Contact',
          cancelButtonText: 'Cancel',
          type: 'warning'
        }).then(() => {
          // console.log(value)
          this.$axios
              .post('/delContact', {
                searchUsername: name,
                ownerUsername: localStorage.getItem('username')
              }).then(resp => {
              console.log(resp)
              console.log(resp.data)
              if (resp.data.code == 200) {
                this.$message({
                type: 'success',
                message: 'Contact Deleted.'
              })
                this.loadContacts()
              }
            })
        }).catch(() => {
          this.$message({
            type: 'info',
            message: 'Cancelled to delete the contact.'
          })
        })
    }
  }
})
</script>

<style scoped>
  #card {
    display: flex;
    align-items: center;
    height: 100px;
    width: 100vw;
    border-bottom: 1px solid;
    border-color: #DCDCDC;
    text-align: center;
    max-width: 100%;
    overflow-x: hidden;
  }

  #avatar {
    display: table;
    width: 100px;
    margin-left: 20px;
  }

  #name {
    flex: 1;
    text-align: left;
  }

  #btnDel {
    margin-right: 20px;
  }

</style>
