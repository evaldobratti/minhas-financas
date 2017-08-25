<template>
  <div>
    <v-snackbar
          :success="context == 'success'"
          :primary="context == 'primary'"
          :error="context == 'error'"
          :timeout="timeout"
          :top="true"
          v-model="showing">
        {{ text }}
        <v-btn flat dark @click.native="showing = false">Close</v-btn>
      </v-snackbar>
  </div>
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
