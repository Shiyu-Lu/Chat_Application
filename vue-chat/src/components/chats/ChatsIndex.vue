<template>
  <body>
    <el-row class="box">
      <el-col :span="3">
        <div class="grid-content">
          <!-- Navigation Menu -->
          <nav-menu></nav-menu>
        </div>
      </el-col>
      <el-col :span="6">
        <div>
          <el-row id="rowSearch">
            <!-- Add Contacts -->
            <el-input class="inputUsername" v-model="username" size="small" placeholder="Search contact" clearable>
              <el-button slot="append" icon="el-icon-search" v-on:click="searchContact"></el-button>
            </el-input>
          </el-row>
          <el-row id="rowContact" v-if="true">
            <!-- Contacts -->
            <el-scrollbar wrap-class="list" :native="false">
              <single-msg-card  @userSelect="showMsgContent" ref="singleMsgCard"></single-msg-card>
            </el-scrollbar>
          </el-row>
        </div>
      </el-col>
      <el-col :span="15">
        <div>
          <!-- History Message Box -->
          <el-row id="msgBox" v-if="true">
            <!-- Add Contacts -->
            <el-scrollbar wrap-class="msglist" :native="false">
              <single-msg-stream ref="msgStream"></single-msg-stream>
            </el-scrollbar>
          </el-row>
          <!-- Input Box -->
          <el-row id="inputBox">
            <el-scrollbar wrap-class="textlist" :native="false">
              <el-input id="input" type="textarea" v-model="msgContent" autosize></el-input>
            </el-scrollbar>
            <el-button id="btnSend" v-on:click="sendMsg">Send</el-button>
          </el-row>
        </div>
      </el-col>
    </el-row>
  </body>

</template>

<script>
  import NavMenu from '../common/NavMenu'
  import SingleMsgCard from './SingleMsgCard'
  import SingleMsgStream from './SingleMsgStream'

  export default {
    name: 'ChatsIndex',
    components: {NavMenu,SingleMsgCard,SingleMsgStream},
    data () {
      return {
        username: '',
        msgContent: '',
        responseResult: [],
        selectedUser: ''
      }
    },
    methods: {
      showMsgContent() {
        var _this = this
        this.$axios
          .post('/singlemsgstream', {
            ownerUsername: localStorage.getItem('username'),
            searchUsername: this.$refs.singleMsgCard.selectedUser
          }).then(resp => {
            if (resp.data.code == 200) {
              _this.$refs.msgStream.msgStreams = resp.data
          }}).catch(() => {
            console.log('Fail to show msgContent.')
          })
      },
      searchContact() {
        var _this = this
        console.log('searching')
        this.$axios
          .post('/searchcontact', {
            searchUsername: this.username,
            ownerUsername: localStorage.getItem('username')
          }).then(resp => {
            if (resp.data.code == 200) {
              this.$notify({
              type: 'success',
              title: 'Contact existed.'
            })
            } else {
              this.$notify.error({
                title: 'Contact not existed.'
              })
          }}).catch(() => {})
      },
      sendMsg () {
        var _this = this
        this.$axios
          .post('/sendmsg', {
            msgContent: this.msgContent,
            ownerUsername: localStorage.getItem('username'),
            searchUsername: this.$refs.singleMsgCard.selectedUser
          }).then(resp => {
            if (resp.data.code == 200) {
              console.log('Message sent.')
              // _this.$refs.msgStream.msgStreams = resp.data
          }}).catch(() => {
            this.$notify.error({
                title: 'Error',
                message: 'Failed to send. Please try again later.'
              });
          })
      }
    }
  }
</script>

<style>
  @import "../../assets/css/home.css";
  @import "../../assets/css/contacts.css";

  .inputUsername{
    margin-left: 20px;
    margin-right: 20px;
    width: 100%;
  }

  #msgBox {
    border-left: 1px solid rgb(217, 217, 217);
    display: flex;
    max-width: 100vw;
    height: 554px;
    overflow: hidden;
  }

  #inputBox {
    display: flex;
    height: calc(100vh - 554px);
    max-width: 100vw;
    overflow: hidden;
  }

  .msglist {
    max-height: 550px;
    max-width: 100%;
    overflow-x: hidden;
  }

  .textlist {
    border-top: 1px solid rgb(217, 217, 217);
    border-left: 1px solid rgb(217, 217, 217);
    max-height: calc(100vh - 550px);
    max-width: 100%;
    overflow-x: hidden;
  }

  #input {
    width: 100vw;
    max-width: 100%;
    border-width:0px;
    border: none !important;
    border: 0;
    display: table;
    flex: 1;
    overflow-y: scroll;
    margin: 10px 10px 10px 10px;
  }

  #btnSend {
    float: right;
    z-index: 999;
    position: absolute;
    right: 3%;
    bottom: 7%;
    background-color: rgb(75, 185, 79);
    color: white;
  }

  #btnSend:hover {
    border-color: rgb(75, 185, 79);
    background-color:	white;
    color: rgb(75, 185, 79);
}
</style>

