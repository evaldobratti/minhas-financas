<template>
  <div>
    <v-snackbar 
          :timeout="5000"
          :top="true"
          :right="true"
          v-model="snackbar">
        {{ text }}
        <v-btn flat class="pink--text" @click.native="snackbar = false">Close</v-btn>
      </v-snackbar>
  </div>
</template>

<script>
export default {
  created() {
    this.$root.$on('snack', (data) => {
      this.snackbar = true;
      this.text = data.text;
    });
  },
  data() {
    return {
      snackbar: false,
      text: ''
    }
  }

}
</script>

<style>
  
</style>
