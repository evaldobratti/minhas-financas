<template>
  <v-layout row>
    <v-flex xs6>
      <v-card class="conta-card">
        <date-picker type="month" v-model="mes"/>
        {{gastos}}
      </v-card>
    </v-flex>
  </v-layout>
</template>

<script>
import moment from 'moment';
import DatePicker from '../DatePicker';

export default {
  data() {
    return {
      mes: moment()
    }
  },
  computed: {
    gastos() {
      var contasIds = this.$store.state.conta.asList.map(c => c.id);
      var lancamentos = this.$store.getters.lancamentosDe(contasIds, this.mes.month(), this.mes.year());
      var a = lancamentos.reduce((classificados, lanc) => {
        if (classificados[lanc.idCategoria] != null) {
          classificados[lanc.idCategoria] += lanc.valor;
        } else {
          classificados[lanc.idCategoria] = lanc.valor;
        }
        return classificados
      }, {});

      return a;
    }
  },
  components: {
    DatePicker
  }
}
</script>

<style>

</style>
