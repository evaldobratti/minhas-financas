<template>
  <div :key="this.id">
    <conta-apresentacao  v-if="conta != null" :conta="conta" />
  </div>
</template>

<script>
import ContaApresentacao from '../components/ContaApresentacao'

export default {
  props:['id'],
  data() {
    return {
      conta: null
    }
  },
  created() {
    this.routerUpdated()
  },
  methods: {
    routerUpdated() {
      this.$store.dispatch('contaCarrega', this.$route.params.id).then(conta => {
        this.conta = conta;
      }).catch(() => {
        this.$router.push({
          path: "/404"
        })
      })
    }
  },
  watch: {
    id() {
      this.routerUpdated()
    }
  },
  components: {
    ContaApresentacao
  }
}
</script>

<style>

</style>
