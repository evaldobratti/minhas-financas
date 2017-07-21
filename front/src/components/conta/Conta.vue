<template>
  <ProtectedRoute>
    <v-layout row>
      <v-flex xs12>
    <v-card v-if="conta">
      <v-card-text>
        <v-container fluid>
            <v-layout row>
              <v-flex xs8>
                <h3 class="headline mb-0">
                  {{ conta.nome }}
                  <small class="text-xs-right">Saldo Inicial: {{conta.saldoInicial | currency}}</small>
                </h3>
              </v-flex>
              <v-flex xs1>
                <v-select
                        label="Mês"
                        v-bind:items="meses"
                        v-model="mes"
                        item-text="nome"
                        item-value="ix"
                        max-height="auto">
                </v-select>
              </v-flex>
              <v-flex xs1>
                <v-text-field v-model="ano" label="Ano"></v-text-field>
              </v-flex>
              <v-flex xs2>
                <v-btn>filtrar</v-btn>
              </v-flex>
            </v-layout>
            <LancamentoForm :conta="conta"></LancamentoForm>
            <v-layout row>
              <v-flex offset-xs8 xs2>
                <v-text-field label="Saldo inicial" :value="saldoInicio | currency" readonly></v-text-field>
              </v-flex>
              <v-flex xs2>
                <v-text-field label="Saldo final" :value="saldoFim | currency" readonly></v-text-field>
              </v-flex>
            </v-layout>
            <v-data-table
              :headers="headers"
              :items="lancamentos"
              hide-actions
              class="elevation-5">
              <template slot="items" scope="l">
                <td xs3>{{ l.item.data | date }}</td>
                <td>{{ l.item.local.nome }}</td>
                <td>{{ l.item.categoria.nome }}</td>
                <td class="text-xs-right" :class="css(l.item.valor)">{{ l.item.valor | currency }}</td>
                <td>{{ l.item.efetivada }}</td>
                <td :class="css(l.item.saldoDiario)">{{ l.item.saldoDiario | currency }}</td>

              </template>
            </v-data-table>
        </v-container>
      </v-card-text>
    </v-card>
      </v-flex>
    </v-layout>
  </ProtectedRoute>
</template>

<script>
import { CARREGA_CONTA, contas } from '../../store/conta';
import ProtectedRoute from '../ProtectedRoute';
import LancamentoForm from '../lancamento/LancamentoForm';

export default {
  created() {
    this.$store.dispatch(CARREGA_CONTA, this.$route.params.id);
    this.$store.commit(contas.m.CONTA_SET_PARAMS, {
      contaId: this.$route.params.id, 
      mes: this.mes, 
      ano: this.ano
    });
    this.$store.dispatch(contas.d.CARREGA_CONTA_LANCAMENTOS);
  },
  data() {
    return {
      headers: [
        {
          text: 'Data',
          sortable: false,
          align: 'left'
        },
        {
          text: 'Local',
          sortable: false,
          align: 'left'
        },
        {
          text: 'Categoria',
          sortable: false,
          align: 'left'
        },
        {
          text: 'Valor',
          sortable: false,
          align: 'right'
        },
        {
          text: 'Efetuada',
          sortable: false,
          align: 'left'
        },
        {
          text: 'Saldo',
          sortable: false,
          align: 'left'
        },
      ],
      meses: [
        { nome: 'Janeiro', ix: 1},
        { nome: 'Fevereiro', ix: 2},
        { nome: 'Março', ix: 3},
        { nome: 'Abril', ix: 4},
        { nome: 'Maio', ix: 5},
        { nome: 'Junho', ix: 6},
        { nome: 'Julho', ix: 7},
        { nome: 'Agosto', ix: 8},
        { nome: 'Setembro', ix: 9},
        { nome: 'Outubro', ix: 10},
        { nome: 'Novembro', ix: 11},
        { nome: 'Dezembro', ix: 12},
      ],
      mes: 7,
      ano: 2017
    }
  },
  computed: {
    conta() {
      return this.$store.state.conta.conta;
    },
    lancamentos() {
      return this.$store.state.conta.lancamentos;
    },
    saldoInicio() {
      return this.$store.state.conta.saldoInicio;
    },
    saldoFim() {
      return this.$store.state.conta.saldoFim;
    }
  },
  methods: {
    css(valor) {
      return { 
        'red--text': valor < 0, 
        'blue--text text--darken-3': valor > 0
      }
    }
  },
  components: {
    ProtectedRoute,
    LancamentoForm
  }
}
</script>

<style>
  
</style>
  