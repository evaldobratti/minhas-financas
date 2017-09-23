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
              <v-flex xs1>
                <v-btn class="small-btn" @click="mesRetrocede()"><v-icon>chevron_left</v-icon></v-btn>
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
              <v-flex xs1>
                <v-btn class="small-btn" @click="mesAvanca()"><v-icon>chevron_right</v-icon></v-btn>
              </v-flex>
              <v-flex xs2>
                <v-btn @click="filtrar">filtrar</v-btn>
              </v-flex>
            </v-layout>
            <LancamentoForm :conta="conta"></LancamentoForm>
            <v-data-table
              :items="lancamentos"
              hide-actions
              class="elevation-5 lancamentos">
              <template slot="headers" scope="props">
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
                  <th style="text-align: right; width: 120px">
                    Valor
                  </th>
                  <th style="width: 10px">
                    Efetuada
                  </th>
                  <th style="text-align: right; width: 120px">
                    Saldo
                  </th>
                  <th style="width: 70px"></th>
                </tr>
              </template>
              <template slot="items" scope="l">
                <LancamentoLinha :lancamento="l.item" 
                  @novaRecorrencia="novaRecorrencia(l.item)" 
                  @novoParcelamento="novoParcelamento(l.item)"
                  @deleteLancamento="deleteLancamento(l.item)"
                  @efetiva="efetiva(l.item)"
                  />
              </template>
            </v-data-table>
        </v-container>
      </v-card-text>
    </v-card>
      </v-flex>
    </v-layout>
    <v-dialog v-model="recorrenciaDialog" width="500px">
      <v-card>
        <v-card-title>
          <div class="headline">Nova recorrência</div>
        </v-card-title>
        <v-card-text>
          <RecorrenciaForm ref="recorrenciaForm" :lancamento="lancamentoAcao" @cadastrado="recorrenciaDialog = false"></RecorrenciaForm>
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

import axios from 'axios';
export default {
  created() {
    this.$store.dispatch(CARREGA_CONTA, this.$route.params.id);
    this.$store.dispatch(lancamentos.d.LANCAMENTO_LOAD);
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
      mes: new Date().getMonth() + 1,
      ano: new Date().getFullYear(),
      lancamentoAcao: null,
      recorrenciaDialog: false,
      parcelamentoDialog: false
    }
  },
  computed: {
    conta() {
      return this.$store.state.conta.conta;
    },
    lancamentos() {
      return this.$store.getters.lancamentosDe(this.$route.params.id, this.mes, this.ano);
    },
    saldoInicio() {
      return this.$store.state.conta.saldoInicio;
    },
    saldoFim() {
      return this.$store.state.conta.saldoFim;
    }
  },
  methods: {
    mesAvanca() {
      this.mes += 1;
      this.filtrar();
    },
    mesRetrocede() {
      this.mes -= 1;
      this.filtrar();
    },
    filtrar() {
      if (this.mes > 12) {
        this.mes = 1;
        this.ano += 1;
      }

      if (this.mes < 1) {
        this.mes = 12;
        this.ano -= 1;
      }
    },
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
    efetiva(lancamento) {
      axios.put('/api/lancamentos', lancamento).then(res => {
        console.info('foi lul', res)
      }).catch(err => {
        console.error('nheca', err);
      })
    },
  },
  components: {
    ProtectedRoute,
    LancamentoForm,
    LancamentoLinha,
    RecorrenciaForm,
    ParcelamentoForm
  }
}
</script>

<style>
.number-input input {
  text-align: right;
}

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
