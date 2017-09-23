<template>
<tr>
  <td xs3>
    {{ lancamento.data | date }}
  </td>
  <td>
    <LocalAutoComplete v-show="isEditando" ref="descricao" v-model="local" @blur="blur('descricao')" class="inlineEdit"></LocalAutoComplete>
    <span v-show="!isEditando" @click="editando('descricao')">{{ lancamento.local.nome }} <span v-if="lancamento.motivo && lancamento.motivo.complementoDescricao">{{lancamento.motivo.complementoDescricao}} </span> </span>
  </td>
    <td>{{ lancamento.categoria.nome }}</td>
    <td class="text-xs-right" :class="css(lancamento.valor)">
        {{ lancamento.valor | currency }}
    </td>
    <td><v-checkbox v-if="lancamento.efetivada != null" v-model="lancamento.efetivada"></v-checkbox></td>
    <td class="text-xs-right" :class="css(lancamento.saldoDiario)">{{ lancamento.saldoDiario | currency }}</td>
    <td>
        <v-menu bottom right>
        <v-btn icon slot="activator">
            <v-icon>more_vert</v-icon>
        </v-btn>
        <v-list>
            <v-list-tile @click="novaRecorrencia(lancamento)" :disabled="lancamento.motivo != null">
            <v-list-tile-title>
                <v-btn primary fab small dark class="small-fab-btn">
                <v-icon>refresh</v-icon>
                </v-btn>
                Recorrencia
            </v-list-tile-title>
            </v-list-tile>
            <v-list-tile @click="novoParcelamento(lancamento)" :disabled="lancamento.motivo != null">
            <v-list-tile-title>
                <v-btn primary fab small dark class="small-fab-btn">
                <v-icon>refresh</v-icon>
                </v-btn>
                Parcelamento
            </v-list-tile-title>
            </v-list-tile>
            <v-list-tile @click="deleteLancamento(lancamento)">
            <v-list-tile-title>
                <v-btn primary fab small dark class="small-fab-btn red">
                <v-icon>remove</v-icon>
                </v-btn>
                Apagar
            </v-list-tile-title>
            </v-list-tile>
            <v-list-tile @click="efetiva(lancamento)" :disabled="lancamento.id != null" >
            <v-list-tile-title>
                <v-btn primary fab small dark class="small-fab-btn green">
                <v-icon>add</v-icon>
                </v-btn>
                Efetivar
            </v-list-tile-title>
            </v-list-tile>
        </v-list>
        </v-menu>
    </td>
    <v-dialog v-model="recorrenciaDialog" width="500px">
      <v-card>
        <v-card-title>
          <div class="headline">Nova recorrência</div>
        </v-card-title>
        <v-card-text>
          <RecorrenciaForm ref="recorrenciaForm" :lancamento="lancamento" @cadastrado="recorrenciaDialog = false"></RecorrenciaForm>
        </v-card-text>
      </v-card>
    </v-dialog>
    <v-dialog v-model="parcelamentoDialog">
      <v-card>
        <v-card-title>
          <div class="headline">Novo Parcelamento</div>
        </v-card-title>
        <v-card-text>
          <ParcelamentoForm :lancamento="lancamento" @cadastrado="parcelamentoDialog = false"></ParcelamentoForm>
        </v-card-text>
      </v-card>
    </v-dialog>
</tr>
</template>

<script>
import { lancamentos } from '../../store/lancamento';
import mapGetSet from '../../store/mapGetSet';
import LocalAutoComplete from './LocalAutoComplete';
import RecorrenciaForm from '../recorrencia/RecorrenciaForm';
import ParcelamentoForm from '../parcelamento/ParcelamentoForm';

export default {
  props: ['lancamento'],
  data() {
    return {
      isEditando: false,
      recorrenciaDialog: false,
      parcelamentoDialog: false
    }
  },
  methods: {
    deleteLancamento(lancamento) {
      this.$store.dispatch(lancamentos.d.REMOVE_LANCAMENTO, lancamento);
    },
    novaRecorrencia(lancamento) {
      this.$refs.recorrenciaForm.updateLancamento(lancamento);
      this.recorrenciaDialog = true;
    },
    novoParcelamento(lancamento) {
      console.info(this.lancamento);
      this.parcelamentoDialog = true;
    },
    efetiva(lancamento) {
      axios.put('/api/lancamentos', lancamento).then(res => {
        console.info('foi lul', res)
      }).catch(err => {
        console.error('nheca', err);
      })
    },
    css(valor) {
      return {
        'red--text': valor < 0,
        'blue--text text--darken-3': valor >= 0
      }
    },
    editando(campo) {
      this.$store.commit(lancamentos.m.SET_EDICAO, this.lancamento);
      this.isEditando = true;
    },
    blur(campo) {
      this.isEditando = false;
    }
  }, 
  computed: {
    local: {
      get() {
        return this.$store.getters.lancamentoEdicao.local;
      },
      set(local) {
        if (local.id != this.$store.getters.lancamentoEdicao.local.id) {
          this.$store.commit(lancamentos.m.EDICAO_SET_LOCAL, local);
          this.$store.dispatch(lancamentos.d.LANCAMENTO_EDICAO_SUBMIT)
        }
      }
    }
  },
  components: {
    LocalAutoComplete,
    RecorrenciaForm,
    ParcelamentoForm
  }
}
</script>

<style>
.inlineEdit .input-group--select .input-group__selections__comma{
  color: red;
  font-size: 13px;
}
</style>
