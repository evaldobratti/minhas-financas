<template>
  <v-layout row>
      <v-flex xs6>
        <v-card class="conta-card">
          <date-picker type="month" v-model="mes"/>
          <line-chart
            :chart-data="saldos"
            :options="options"
            :height="300"
            chart-id="canvas2"
            ></line-chart>
        </v-card>
      </v-flex>
  </v-layout>
</template>

<script>
import * as _ from 'underscore';
import { Bar } from 'vue-chartjs';
import LineChart from '../charts/LineChart';
import DatePicker from '../DatePicker';
import moment from 'moment';
import currency from '../../filters/currency';
import date from '../../filters/date';

export default {
  data() {
    var vm = this;
    return {
      mes: moment(),
      options: {
        responsive: true,
        legend: {
          display: false
        },
        tooltips: { 
          mode: 'index', 
          intersect: false,
          callbacks: {
            title: function([item], data) {
              let lancamento = vm.saldos.lancamentos[item.index];

              return lancamento.local + ' em ' + date(lancamento.data);
            },
            label: function(item, data) {
              let lancamento = vm.saldos.lancamentos[item.index];

              return currency(lancamento.valor)
            },
            footer: function([item], data) {
              let lancamento = vm.saldos.lancamentos[item.index];

              return 'Saldo: ' + currency(lancamento.saldoDiario)
            },
          }
        }
      }
    }
  },
  computed: {
    saldos() {
      var contasIds = this.$store.state.conta.asList.map(c => c.id);
      var lancamentos = this.$store.getters.lancamentosDe(contasIds, this.mes.month(), this.mes.year());
      var saldos = lancamentos.map(l => {
        return {
          data: l.data, 
          saldo: l.saldoDiario,
        }
      }); 
      
      var labels = saldos.map(s => s.data.date() + '/' + s.data.month());
      var datasets = {
        fill: false,
        label: 'saldo',
        pointBackgroundColor: lancamentos.map(l => l.valor > 0 ? 'blue' : 'red'),
        borderColor: '#999',
        borderWidth: '1',
        data: saldos.map(s => s.saldo )
      };
      return {
        labels, 
        
        datasets: [datasets],
        lancamentos
      };
    }
  },
  components: {
    LineChart,
    DatePicker
  }
}
</script>

<style>

</style>
