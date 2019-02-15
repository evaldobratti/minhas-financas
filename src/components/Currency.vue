<template>
  <span v-if="value != undefined" :class="classes">{{formatado}}</span>
</template>

<script>
export default {
  props: ['value'],
  computed: {
    asNumber() {
      return Number(this.value)
    },
    formatado() {
      return this.asNumber.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' }).replace('R$', 'R$ ')
    },
    classes() {
      return {
        debito: this.asNumber < 0,
        credito: this.asNumber > 0
      }
    }
  }
}
</script>

<style>

.debito {
  color: red;
}

.credito {
  color: blue;
}

</style>
