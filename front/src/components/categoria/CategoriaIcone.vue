<template>
  <span>
    <v-icon @click="$emit('abreFecha')" v-if="categoria && categoria.filhas && categoria.filhas.length === 0">insert_drive_file</v-icon>
    <v-icon @click="$emit('abreFecha')" v-else-if="!aberto">folder</v-icon>
    <v-icon @click="$emit('abreFecha')" v-else>folder_open</v-icon>
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
