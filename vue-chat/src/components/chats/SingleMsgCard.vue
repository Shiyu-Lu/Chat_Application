<template>
  <div>
    <div
      id="card"
      v-for="(contact, i) in contacts"
      :key="contact.index"
      v-bind:content="contact"
      class="accident-item flex-row"
      @mouseenter="enter(i)"
      @mouseleave="leave()"
      @click="currentCheckIdx=i;handleSelect(contact.convUserName)"
      :class="[currentIdx==i?'itemBg':'',currentCheckIdx==i?'checkedItemBg':'']">
      <div id="avatar" ><el-avatar icon="el-icon-user-solid"></el-avatar></div>
      <el-col class="infoBox">
        <h5 class="info" id='name'>{{contact.convUserName}}</h5>
        <h6 class="info" id='lastMsg'>{{contact.lastMsgContent}}</h6>
      </el-col>
    </div>
  </div>
</template>

<script>
export default({
  name: 'SingleMsgCard',
  data() {
    return {
      currentIdx:null,
      currentCheckIdx:null,
      selectedUser: '',
      timer: 1000,
      contacts: []
    }
  },
  created() {
    window.setInterval(() => this.loadMsgList(),this.timer);
  },
  mounted() {
    this.loadMsgList()
  },
  destroyed() {
    window.clearInterval(this.timer)
  },
  methods: {
    handleSelect(key) {
      this.selectedUser = key;
      console.log(key)
      this.$emit('userSelect')
      localStorage.setItem('selectedUser', key)
    },
    enter(idx){
      // console.log(idx);
      this.currentIdx = idx
    },
    leave(){
      this.currentIdx =null
    },
    loadMsgList () {
        var _this = this
        // console.log('loading the msglist')
        this.$axios.post('/msglist',{
          ownerUsername: localStorage.getItem('username')
        }).then(resp => {
          var json = JSON.stringify(resp.data);
          _this.contacts = JSON.parse(json);
          _this.$forceUpdate()
          this.$forceUpdate()
        }).catch(failResponse => {
          console.log(failResponse)
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

  .itemBg {
    background: rgb(240, 240, 240);
  }

  .checkedItemBg {
    background-color: rgb(225, 225, 225) !important;
  }

  #avatar {
    display: table;
    width: 100px;
    margin-left: 20px;
  }

  .infoBox{
    text-align: left;
    width: 200px;
    height: 55px;
  }

  .info {
    white-space:nowrap;
    overflow:hidden;
    text-overflow:ellipsis;
  }

  #name {
    margin-top: 0;
    margin-left: 0;
  }

  #lastMsg {
    margin-top: -5px;
    margin-bottom: 0;
    color: rgb(168, 168, 168);
  }

</style>
