<template>
  <v-layout row>
    <v-flex xs6>
      <v-card class="conta-card">
        <date-picker type="month" v-model="mes"/>
        <pie-chart :chart-data="gastos" />
        {{gastos}}
      </v-card>
    </v-flex>
  </v-layout>
</template>

<script>
import moment from 'moment';
import DatePicker from '../DatePicker';
import PieChart from '../charts/PieChart';

function getRandomColor() {
    var letters = '0123456789ABCDEF';
    var color = '#';
    for (var i = 0; i < 6; i++ ) {
        color += letters[Math.floor(Math.random() * 16)];
    }
    return color;
}

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
      var categorias = [];
      lancamentos.forEach(lanc => {
        if (!lanc.id)
          return;

        if (lanc.valor > 0)
          return;

        var categoria = categorias.find(f => f.idCategoria == lanc.idCategoria);
        if (categoria == null) {
          categorias.push({
            idCategoria: lanc.idCategoria,
            categoria: this.$store.getters.getCategoria(lanc.idCategoria),
            valor: -lanc.valor
          })}
        else {
          categoria.valor += -lanc.valor;
        }
      });
      
      return {
        datasets: [{
          data: categorias.map(f => f.valor),
          backgroundColor: categorias.map(f => getRandomColor())
        }],
        labels: categorias.map(f => f.categoria.nome)
      };
    }
  },
  components: {
    DatePicker,
    PieChart
  }
}
</script>

<style>

</style>
