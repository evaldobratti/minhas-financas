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
                <v-checkbox v-model="incluiSaldoAnterior" label="Saldo anterior"></v-checkbox>
              </v-flex>
              <v-flex xs4>
                <DatePicker v-model="dataFiltro" type="month"></DatePicker>
              </v-flex>
            </v-layout>
            <v-tabs
              class="small-tab"
              v-model="tabLancamento"
              grow
            >
              <v-tabs-slider></v-tabs-slider>
              <v-tab key="avulso">
                Lançamento
              </v-tab>
              <v-tab key="transferencia">
                Transferência
              </v-tab>
            </v-tabs>
            <v-tabs-items v-model="tabLancamento">
              <v-tab-item key="avulso">
                <LancamentoForm :id-conta="conta.id" />
              </v-tab-item>
              <v-tab-item key="transferencia">
                <v-card flat>
                  <v-card-text>
                    <lancamento-transferencia-form :id-conta-origem="conta.id"/>
                  </v-card-text>
                </v-card>
              </v-tab-item>
            </v-tabs-items>

            <v-container style="max-height: 400px" class="scroll-y">
              <v-layout>
            <v-data-table
              :items="lancamentos"
              hide-actions
              class="elevation-5 lancamentos">
              <template slot="headers" slot-scope="props">
                <tr>
                  <th>
                    Data
                  </th>
                  <th style="width: 300px">
                    Local
                  </th>
                  <th>
                    Categoria
                  </th>
                  <th style="text-align: right; width: 140px">
                    Valor
                  </th>
                  <th style="text-align: right; width: 140px">
                    Saldo
                  </th>
                  <th>Efetivada</th>
                  <th></th>
                  <th></th>
                </tr>
              </template>
              <template slot="items" slot-scope="l">
                <LancamentoLinha :key="l.item.tempId || l.item.id"
                  :lancamento="l.item"
                  @novaRecorrencia="novaRecorrencia(l.item)" 
                  @novoParcelamento="novoParcelamento(l.item)"
                  @deleteLancamento="deleteLancamento(l.item)"
                  @efetiva="efetiva(l.item)"
                  @trocaConta="trocaConta(l.item)"
                  @transferencia="transferencia(l.item)"
                  />
              </template>
            </v-data-table>
              </v-layout>
            </v-container>
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
    <v-dialog v-model="transferenciaDialog" lazy>
      <v-card>
        <v-card-title>
          <div class="headline">Transferência</div>
        </v-card-title>
        <v-card-text>
          <transferencia-form :key="lancamentoAcao ? lancamentoAcao.id : 'null'" :lancamento="lancamentoAcao" @cadastrado="transferenciaDialog = false"></transferencia-form>
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
import TransferenciaForm from '../transferencia/TransferenciaForm';
import TrocaConta from './TrocaConta';
import moment from 'moment';
import DatePicker from '../DatePicker';
import Vue from 'vue';
import axios from 'axios';
import { SNACKS } from '../../store/snacks';
import LancamentoTransferenciaForm from '../transferencia/LancamentoTransferenciaForm';

export default Vue.extend({
  data() {
    return {
      dataFiltro: moment(),
      lancamentoAcao: null,
      recorrenciaDialog: false,
      parcelamentoDialog: false,
      trocaContaDialog: false,
      transferenciaDialog: false,
      incluiSaldoAnterior: true,
      tabLancamento: '',
      turnReactive: {}
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
      var inicio = this.dataFiltro.clone().startOf('month');
      var fim = this.dataFiltro.clone().endOf('month');
      
      if (this.conta) {
        const ls = this.$store.getters.lancamentosDe([ this.conta.id ], inicio, fim, this.incluiSaldoAnterior);
        for (const l of ls) {
          this.turnReactive = l;
        }
        return ls;
      }
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
      this.$store.dispatch(lancamentos.d.REMOVE_LANCAMENTO, lancamento).then(msg => {
        this.$store.commit(SNACKS.m.UPDATE_SUCESSO, msg)
      }).catch(err => {
        this.$store.commit(SNACKS.m.UPDATE_ERRO, msg)
      }); 
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
    transferencia(lancamento) {
      this.lancamentoAcao = lancamento;
      this.transferenciaDialog = true;
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
    DatePicker,
    TransferenciaForm,
    LancamentoTransferenciaForm
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

.small-tab .tabs__container--grow {
  height: 30px
}

</style>
