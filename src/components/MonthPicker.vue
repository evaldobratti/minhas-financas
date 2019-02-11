<template>
  <v-menu
    v-model="dialog"
    :close-on-content-click="false"
    :nudge-right="40"
    lazy
    transition="scale-transition"
    offset-y
    full-width
    min-width="290px"
  >
    <v-text-field
      slot="activator"
      v-model="date"
      prepend-icon="event"
      readonly
    ></v-text-field>
    <v-date-picker v-model="date" type="month"></v-date-picker>
  </v-menu>

  
</template>

<script>
import moment from 'moment'

export default {
  props: ['value'],
  data() {
    return {
      dialog: false,
      date: null
    }
  },
  created() {
    this.date = this.value.format('YYYY-MM')
  },
  watch: {
    date() {
      this.$emit('input', moment(this.date))
      this.dialog = false
    },
    value() {
      this.date = this.value.format('YYYY-MM')
    }
  }

}
</script>

<style>

</style>
