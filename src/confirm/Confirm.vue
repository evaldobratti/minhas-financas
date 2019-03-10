<template>
  <v-dialog @input="change" value="true" :max-width="width" @keydown.esc="choose(false)">
    <v-card>
      <v-card-title class="headline">{{title}}</v-card-title>
      <v-card-text v-html="message"/>
      <v-card-actions>
        <v-spacer/>
        <v-btn v-if="Boolean(buttonFalseText)" :color="buttonFalseColor" flat @click="choose(false)">{{ buttonFalseText }}</v-btn>
        <v-btn v-if="Boolean(buttonTrueText)" :color="buttonTrueColor" flat @click="choose(true)">{{ buttonTrueText }}</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
//https://github.com/yariksav/vuetify-confirm/blob/master/src/Confirm.vue
export default {
  props: {
    buttonTrueText: {
      type: String,
      default: 'Sim'
    },
    buttonFalseText: {
      type: String,
      default: 'Não'
    },
    buttonTrueColor: {
      type: String,
      default: 'primary'
    },
    buttonFalseColor: {
      type: String,
      default: 'grey'
    },
    color: {
      type: String,
      default: 'warning'
    },
    icon: {
      type: String,
      default: 'warning'
    },
    message: {
      type: String
    },
    title: {
      type: String,
      default: 'Confirme'
    },
    width: {
      type: Number,
      default: 350
    },
  },
 data () {
    return {
      value: false
    }
  },
  methods: {
    choose (value) {
      this.$emit('result', value)
      this.value = value
      this.$destroy()
    },
    change (res) {
      this.$destroy()
    }
  }
}
</script>

<style>

</style>
