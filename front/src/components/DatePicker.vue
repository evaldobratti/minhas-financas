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
      <v-date-picker v-model="data" :date-format="formatDate"
                    :formatted-value.sync="dataFormatada" :type="type" no-title scrollable actions></v-date-picker>
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
        this.data = this.value.toDate();
        this.dataFormatada = this.data.toLocaleDateString();
      }
    },
    watch: {
      value(val) {
        if (val) {
          this.data = val.toDate();
          this.dataFormatada = this.data.toLocaleDateString();
        }
      }
    },
    methods: {
      formatDate(val) {
        this.data = new Date(val);
        this.$emit('input', moment(this.data));
        return this.data.toLocaleDateString();
      },
      addMonth() {
        this.formatDate(moment(this.data).add(1, 'month').toDate())
      },
      subtractMonth() {
        this.formatDate(moment(this.data).add(-1, 'month').toDate())
      }
    }
  }
</script>
