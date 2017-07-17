<template>
  <ProtectedRoute>
    <v-card v-if="conta">
      <v-card-text>
        <v-container fluid>
            <v-layout row>
              <v-flex xs8>
                <h3 class="headline mb-0">{{ conta.nome }}</h3>
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
        </v-container>
      </v-card-text>
    </v-card>
  </ProtectedRoute>
</template>

<script>
import { CARREGA_CONTA, CARREGA_CONTA_LANCAMENTOS } from '../../store/conta';
import ProtectedRoute from '../ProtectedRoute';
import LancamentoForm from '../lancamento/LancamentoForm';

export default {
  created() {
    this.$store.dispatch(CARREGA_CONTA, this.$route.params.id);
    this.$store.dispatch(CARREGA_CONTA_LANCAMENTOS, {
      contaId: this.$route.params.id, 
      mes: this.mes, 
      ano: this.ano
    });
  },
  data() {
    return {
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
      return this.$store.state.conta.contaAtual;
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
  