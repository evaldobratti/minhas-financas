<template>
  <div>
  <v-card>
    <v-card-text>
      <v-layout row wrap>
        <v-flex xs8>
          <v-checkbox v-for="conta in contas" :key="conta.id" :label="conta.nome" v-model="contasSelecionadas" :value="conta.id" />
        </v-flex>
        <v-flex xs4>
          <DatePicker v-model="dataFiltro" type="month"></DatePicker>
        </v-flex>
      </v-layout>
    </v-card-text>
  </v-card>
  <v-card>
    <v-card-text>
      <v-data-table
              :items="lancamentos"
              hide-actions
              class="elevation-5 lancamentos">
              <template slot="headers" slot-scope="props">
                <tr>
                  <th style="width: 10px">
                    Data
                  </th>
                  <th>
                    Local
                  </th>
                  <th style="width: 130px">
                    Categoria
                  </th>
                  <th style="text-align: right; width: 140px">
                    Valor
                  </th>
                  <th style="text-align: right; width: 140px">
                    Saldo
                  </th>
                  <th  style="width: 40px" ></th>
                  <th  style="width: 40px" ></th>
                </tr>
              </template>
              <template slot="items" slot-scope="l">
                <LancamentoLinha :lancamento="l.item" />
              </template>
            </v-data-table>
      </v-card-text>
    </v-card>
  </div>
</template>

<script>
import LancamentoLinha from '../lancamento/LancamentoLinha';
import moment from 'moment';
import DatePicker from '../DatePicker';
export default {
  data() {
    return {
      dataFiltro: moment(),
      contasSelecionadas: [],
      lancamentos: []
    }
  },
  methods: {
    refreshLancamentos() {
      var mes = this.dataFiltro.month();
      var ano = this.dataFiltro.year();
      this.lancamentos = this.$store.getters.lancamentosDe(this.contasSelecionadas, mes, ano);
    }
  },
  watch: {
    contasSelecionadas() {
      this.refreshLancamentos();
    },
    dataFiltro() {
      this.refreshLancamentos();
    }
  }, 
  computed: {
    contas() {
      return this.$store.getters.getContas;
    }
  },
  components: {
    LancamentoLinha,
    DatePicker
  }

}
</script>

<style>

</style>
