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
      :label="label"
      readonly
    ></v-text-field>
    <v-date-picker v-model="date" @input="dialog = false"></v-date-picker>
  </v-menu>
</template>

<script>
import moment from 'moment'

export default {
  props: ['value', 'label'],
  data() {
    const hoje = new Date()
    return {
      date: `${hoje.getFullYear()}-${hoje.getMonth() + 1}-${hoje.getDate()}`,
      dialog: false
    }
  },
  created() {
    this.date = this.value.format('YYYY-MM-DD')
    this.$emit('input', )
  },
  watch: {
    date() {
      this.$emit('input', moment(this.date))
    },
    value() {
      this.date = this.value.format('YYYY-MM-DD')
    }
  }
}
</script>

<style>

</style>
