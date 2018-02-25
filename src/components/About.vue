<template>
  <div>
    Versão: {{ version }}
  </div>
</template>

<script>
export default {
  computed: {
    version() {
      return this.$store.getters.version;
    }
  }
}
</script>

<style>

</style>
