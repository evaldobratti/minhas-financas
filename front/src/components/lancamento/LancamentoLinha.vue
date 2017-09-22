<template>
<tr>
  <td xs3>
    {{ lancamento.data | date }}
  </td>
  <td>
    <LocalAutoComplete v-show="isEditando" ref="descricao" v-model="local" @blur="blur('descricao')" class="inlineEdit"></LocalAutoComplete>
    <span v-show="!isEditando" @click="editando('descricao')">{{ lancamento.local.nome }}</span>
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
</tr>
</template>

<script>
import { lancamentos } from '../../store/lancamento';
import mapGetSet from '../../store/mapGetSet';
import LocalAutoComplete from './LocalAutoComplete';
export default {
  props: ['lancamento'],
  data() {
    return {
      isEditando: false,
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
      //this.$nextTick(()=> );
    }
  }, 
  computed: {
    local: {
      get() {
        const locais = this.$store.getters.locais;
        const edicao = this.$store.getters.lancamentoEdicao;
        if (edicao.local == null)
          return null;

        return locais.find(l => l.id == edicao.local.id);
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
    LocalAutoComplete
  }
}
</script>

<style>
.inlineEdit .input-group--select .input-group__selections__comma{
  color: red;
  font-size: 13px;
}
</style>
