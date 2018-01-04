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
                </h3>
              </v-flex>
              <v-flex xs4>
              <DatePicker v-model="dataFiltro" type="month"></DatePicker>
              </v-flex>
            </v-layout>
            <LancamentoForm :id-conta="conta.id" />
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
                <LancamentoLinha :lancamento="l.item"
                  @novaRecorrencia="novaRecorrencia(l.item)" 
                  @novoParcelamento="novoParcelamento(l.item)"
                  @deleteLancamento="deleteLancamento(l.item)"
                  @efetiva="efetiva(l.item)"
                  @trocaConta="trocaConta(l.item)"
                  />
              </template>
            </v-data-table>
        </v-container>
      </v-card-text>
    </v-card>
      </v-flex>
    </v-layout>
    <v-dialog v-model="recorrenciaDialog" max-width="500px">
      <v-card>
        <v-card-title>
          <div class="headline">Nova recorrência</div>
        </v-card-title>
        <v-card-text>
          <RecorrenciaForm :lancamento="lancamentoAcao" ref="recorrenciaForm" @cadastrado="recorrenciaDialog = false"></RecorrenciaForm>
        </v-card-text>
      </v-card>
    </v-dialog>
    <v-dialog v-model="parcelamentoDialog">
      <v-card>
        <v-card-title>
          <div class="headline">Novo Parcelamento</div>
        </v-card-title>
        <v-card-text>
          <ParcelamentoForm :lancamento="lancamentoAcao" @cadastrado="parcelamentoDialog = false"></ParcelamentoForm>
        </v-card-text>
      </v-card>
    </v-dialog>
    <v-dialog v-model="trocaContaDialog" lazy>
      <v-card>
        <v-card-title>
          <div class="headline">Troca de conta</div>
        </v-card-title>
        <v-card-text>
          <TrocaConta :key="lancamentoAcao ? lancamentoAcao.id : 'null'" :lancamento="lancamentoAcao" @cadastrado="trocaContaDialog = false"></TrocaConta>
        </v-card-text>
      </v-card>
    </v-dialog>
  </ProtectedRoute>
</template>

<script>
import { CARREGA_CONTA, contas } from '../../store/conta';
import { lancamentos } from '../../store/lancamento';
import ProtectedRoute from '../ProtectedRoute';
import LancamentoForm from '../lancamento/LancamentoForm';
import LancamentoLinha from '../lancamento/LancamentoLinha';
import RecorrenciaForm from '../recorrencia/RecorrenciaForm';
import ParcelamentoForm from '../parcelamento/ParcelamentoForm';
import TrocaConta from './TrocaConta';
import moment from 'moment';
import DatePicker from '../DatePicker';
import Vue from 'vue';
import axios from 'axios';

export default Vue.extend({
  data() {
    return {
      dataFiltro: moment(),
      lancamentoAcao: null,
      recorrenciaDialog: false,
      parcelamentoDialog: false,
      trocaContaDialog: false
    }
  },
  created() {
    this.$store.dispatch(lancamentos.d.LANCAMENTO_LOAD, this.$route.params.id);
  },
  computed: {
    conta() {
      return this.$store.getters.getConta(this.$route.params.id);
    },
    lancamentos() {
      var mes = this.dataFiltro.month();
      var ano = this.dataFiltro.year();
      if (this.conta)
        return this.$store.getters.lancamentosDe([ this.conta.id ], mes, ano);
      else
        return []
    },
    saldoInicio() {
      return this.$store.state.conta.saldoInicio;
    },
    saldoFim() {
      return this.$store.state.conta.saldoFim;
    }
  },
  methods: {
    deleteLancamento(lancamento) {
      this.$store.dispatch(lancamentos.d.REMOVE_LANCAMENTO, lancamento);      
    },
    novaRecorrencia(lancamento) {
      this.lancamentoAcao = lancamento;
      this.recorrenciaDialog = true;
    },
    novoParcelamento(lancamento) {
      this.lancamentoAcao = lancamento;
      this.parcelamentoDialog = true;
    },
    trocaConta(lancamento) {
      this.lancamentoAcao = lancamento;
      this.trocaContaDialog = true;
    },
    efetiva(lancamento) {
      this.$store.dispatch(lancamentos.d.LANCAMENTO_SUBMIT, lancamento).then(() => {
        this.$emit('submetido');
      });
    },
  },
  components: {
    ProtectedRoute,
    LancamentoForm,
    LancamentoLinha,
    RecorrenciaForm,
    ParcelamentoForm,
    TrocaConta,
    DatePicker
  }
});
</script>

<style>
.small-btn {
  min-width: auto;
}

.lancamentos th {
  text-align: left;
}

table.table tbody tr .small-fab-btn {
  display: none;
}

table.table tbody tr:hover .small-fab-btn {
  display: block;
}
</style>
