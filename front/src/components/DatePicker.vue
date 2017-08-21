<template>
  <v-menu
    lazy
    :close-on-content-click="true"
    v-model="aberto"
    offset-y
    full-width
    :nudge-left="40"
    max-width="290px"
  >
    <v-text-field
      slot="activator"
      label="Data"
      v-model="dataFormatada"
      prepend-icon="event"
      readonly
    ></v-text-field>
    <v-date-picker v-model="data" :date-format="formatDate"
                   :formatted-value.sync="dataFormatada" no-title scrollable actions></v-date-picker>
  </v-menu>
</template>

<script>
  import moment from 'moment';

  export default {
    props: ['value'],
    data () {
      return {
        dataFormatada: null,
        data: null,
        aberto: false
      }
    },
    watch: {
      value() {
        this.data = this.value.toDate();
        this.dataFormatada = this.data.toLocaleDateString();
      }
    },
    methods: {
      formatDate(val) {
        this.data = new Date(val);
        this.$emit('input', moment(this.data));
        return this.data.toLocaleDateString();
      }
    }
  }
</script>
