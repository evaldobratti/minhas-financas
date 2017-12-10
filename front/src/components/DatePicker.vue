<template>
  <div>
    <v-btn v-if="type == 'month'" flat icon @click="subtractMonth()">
      <v-icon>chevron_left</v-icon>
    </v-btn>
    <v-menu
        lazy
        :disabled="disabled"
        :close-on-content-click="true"
        v-model="aberto"
        transition="scale-transition"
        offset-y
        style="width: 140px"
      >
        <v-text-field
          :disabled="disabled"
          slot="activator"
          :label="label"
          v-model="dataFormatada"
          prepend-icon="event"
          readonly
        ></v-text-field>
        <v-date-picker v-model="data" :type="type" no-title scrollable actions></v-date-picker>
      </v-menu>
    <v-btn v-if="type == 'month'" flat icon @click="addMonth()">
      <v-icon>chevron_right</v-icon>
    </v-btn>
  </div>
</template>

<script>
  import moment from 'moment';

  export default {
    props: {
      value: {
        type: Object
      },
      type: {
        type: String,
        default: 'date'
      },
      disabled: {
        type: Boolean,
        default: false
      },
      label: {
        type: String,
        default: 'Data'
      }
    },
    data () {
      return {
        dataFormatada: null,
        data: null,
        aberto: false
      }
    },
    created() {
      if (this.value) {
        this.data = this.value.format('YYYY-MM-DD');
      }
    },
    watch: {
      data(val) {
        if (val != null) {
          const asMoment = moment(this.data);
          this.dataFormatada = asMoment.format('DD/MM/YYYY')
          this.$emit('input', asMoment); 
        }
      }
    },
    methods: {
      addMonth() { 
        this.data = moment(this.data).add(1, 'month').format('YYYY-MM-DD');
      }, 
      subtractMonth() { 
        this.data = moment(this.data).add(-1, 'month').format('YYYY-MM-DD');
      } 
    }
  }
</script>

