<template>
  <div>
    <date-picker type="month" v-model="mes"/>
    <line-chart
      :chart-data="saldos"
      :options="options"
      :width="900"
      ></line-chart>
  </div>
</template>

<script>
import * as _ from 'underscore';
import { Bar } from 'vue-chartjs';
import LineChart from '../charts/LineChart';
import DatePicker from '../DatePicker';
import moment from 'moment';
import currency from '../../filters/currency';

export default {
  data() {
    var vm = this;
    return {
      mes: moment(),
      options: {
        responsive: false, 
        tooltips: { 
          mode: 'index', 
          intersect: false,
          custom: function(model) {
            return model;
          },
          callbacks: {
            label: function(item, data) {
              let lancamento = vm.saldos.lancamentos[item.index];
              return lancamento.local.nome + " " + currency(lancamento.valor) + " " + currency(lancamento.saldoDiario);
            }
          }
        }
      }
    }
  },
  computed: {
    saldos() {
      var contasIds = this.$store.state.conta.list.map(c => c.id);
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
