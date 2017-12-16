<template>
  <v-snackbar
    :color="context"
    :timeout="timeout"
    :top="true"
    v-model="showing">
    <v-icon dark>folder_open</v-icon>
    {{ text }}
    <v-btn 
      flat 
      dark 
      v-if="timeout == 0" 
      @click.native="showing = false">
      Close
    </v-btn>
  </v-snackbar>
</template>

<script>

import { SNACKS } from '../store/snacks';
export default {
  computed: {
    timeout() {
      return this.$store.state.snacks.notification.timeout;
    },
    context() {
      return this.$store.state.snacks.notification.context;
    },
    text() {
      return this.$store.state.snacks.notification.text;
    },
    showing: {
      get() {
        return this.$store.state.snacks.showing;
      },
      set(val) {
        this.$store.commit(SNACKS.m.UPDATE_SHOWING, val);
      }
    }
  }

}
</script>

<style>
  
</style>
