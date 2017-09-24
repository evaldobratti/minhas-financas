<template>
  <span>
    <v-icon @click="$emit('abreFecha')" v-if="categoria && categoria.filhas && categoria.filhas.length === 0">remove</v-icon>
    <v-icon @click="$emit('abreFecha')" v-else-if="!aberto">keyboard_arrow_right</v-icon>
    <v-icon @click="$emit('abreFecha')" v-else>expand_more</v-icon>
  </span>
</template>

<script>
export default {
  props: {
    aberto: {
      type: Boolean,
      default: true,
      required: false
    },
    categoria: [ Object, String ]
  }
}
</script>

<style>
  
</style>
