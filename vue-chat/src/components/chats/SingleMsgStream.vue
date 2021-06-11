<template>
  <div>
    <div
      id="msgInfo"
      class="accident-item flex-row"
      v-for="msgStream in msgStreams"
      :key="msgStream.msgID"
      :class="[msgStream.sender== owner ? 'ownerBg':'otherBg']">
      <el-col class="msgBlock">
        <h5 class="msgLine">{{msgStream.sender}}   at {{msgStream.timeStamp}}</h5>
        <h5 class="msgLine">{{msgStream.msgContent}}</h5>
      </el-col>
    </div>
  </div>
</template>


<script>
export default({
  name: 'SingleMsgStream',
  data() {
    return {
      timer: 500,
      msgStreams: [],
      owner: localStorage.getItem('username')
    }
  },
  created() {
    window.setInterval(() => this.loadMsgStream(),this.timer);
  },
  mounted() {
    this.loadMsgStream()
  },
  destroyed(){
    window.clearInterval(this.timer)
  },
  methods: {
    loadMsgStream () {
      if (localStorage.getItem('selectedUser') != '') {
        var _this = this
        this.$axios.post('/singlemsgstream',{
          ownerUsername: localStorage.getItem('username'),
          searchUsername: localStorage.getItem('selectedUser')
        }).then(resp => {
          console.log('loading msg stream')
          console.log(localStorage.getItem('selectedUser'))
          console.log(resp)
          // let msg = document.getElementById('msgInfo') // 获取对象
          // msg.scrollTop = msg.scrollHeight // 滚动高度

          var json = JSON.stringify(resp.data);
          _this.msgStreams = JSON.parse(json)
          _this.$forceUpdate()
          this.$forceUpdate()
        }).catch(failResponse => {
          console.log(failResponse)
        })
      }
    }
}})
</script>

<style>
  #msgInfo {
    display: flex;
    align-items: center;
    height: 100px;
    width: 100%;
    text-align: center;
    max-width: 100%;
    overflow-x: hidden;
  }

  .msgBlock {
    text-align: left;
    width: 100vw;
    height: 100px;
  }

  .msgLine {
    width: 100vw;
  }

  .ownerBg {
    color: rgb(75, 185, 79) !important;
  }

</style>
