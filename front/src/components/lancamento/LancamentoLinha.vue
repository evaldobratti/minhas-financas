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
    <td class="text-xs-right" :class="css(lancamento.saldoDiario)">{{ lancamento.saldoDiario | currency }}</td>
    <td>
      <v-checkbox style="width: 40px" v-if="lancamento.efetivada != null" v-model="lancamento.efetivada"></v-checkbox>
    </td>
    <td>
      
        <v-menu  style="width: 40px"  bottom right>
        <v-btn icon slot="activator">
            <v-icon>more_vert</v-icon>
        </v-btn>
        <v-list>
            <v-list-tile @click="$emit('novaRecorrencia', lancamento)" :disabled="lancamento.motivo != null">
            <v-list-tile-title>
                <v-btn primary fab small dark class="small-fab-btn">
                <v-icon>refresh</v-icon>
                </v-btn>
                Recorrencia
            </v-list-tile-title>
            </v-list-tile>
            <v-list-tile @click="$emit('novoParcelamento', lancamento)" :disabled="lancamento.motivo != null">
            <v-list-tile-title>
                <v-btn primary fab small dark class="small-fab-btn">
                <v-icon>refresh</v-icon>
                </v-btn>
                Parcelamento
            </v-list-tile-title>
            </v-list-tile>
            <v-list-tile @click="$emit('deleteLancamento', lancamento)">
            <v-list-tile-title>
                <v-btn primary fab small dark class="small-fab-btn red">
                <v-icon>remove</v-icon>
                </v-btn>
                Apagar
            </v-list-tile-title>
            </v-list-tile>
            <v-list-tile @click="$emit('efetiva', lancamento)" :disabled="lancamento.id != null" >
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
      recorrenciaDialog: false,
      parcelamentoDialog: false
    }
  },
  methods: {
    css(valor) {
      return {
        'red--text': valor < 0,
        'blue--text text--darken-3': valor >= 0
      }
    },
    editando(campo) {
      this.$store.commit(lancamentos.m.SET_EDICAO, this.lancamento);
    },
    blur(campo) {
      
    }
  }, 
  computed: {
    isEditando() {
      const edicao = this.$store.getters.lancamentoEdicao ;
      return edicao && this.lancamento.id && edicao.id == this.lancamento.id;
    },
    local: {
      get() {
        if (!this.isEditando)
          return null;
        return this.$store.getters.lancamentoEdicao.local;
      },
      set(local) {
        if (!this.isEditando)
          return;

        if (local.id != this.$store.getters.lancamentoEdicao.local.id) {
          this.$store.commit(lancamentos.m.EDICAO_SET_LOCAL, local);
          this.$store.dispatch(lancamentos.d.LANCAMENTO_EDICAO_SUBMIT);
          this.$store.commit(lancamentos.m.SET_EDICAO, null);
        }
      }
    }
  },
  components: {
    LocalAutoComplete,

  }
}
</script>

<style>
.inlineEdit .input-group--select .input-group__selections__comma{
  color: red;
  font-size: 13px;
}
</style>
