<template>
<tr>
  <td xs3>
    {{ lancamento.data | date }}
  </td>
  <td>
    <span v-show="!isEditando" @click="editando('descricao')">{{ lancamento.local.nome }} <span v-if="lancamento.motivo && lancamento.motivo.complementoDescricao">{{lancamento.motivo.complementoDescricao}} </span> </span>
    <LocalAutoComplete v-show="isEditando" ref="descricao" v-model="lancamento.local" @blur="blur('descricao')" class="inlineEdit"></LocalAutoComplete>
  </td>
    <td>
      <CategoriaAutoComplete v-show="isEditando" ref="categoria" v-model="lancamento.categoria" @blur="blur('categoria')" class="inlineEdit"></CategoriaAutoComplete>
      <span v-show="!isEditando" @click="editando('categoria')">{{ lancamento.categoria.nome }}</span>
    </td>
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
import CategoriaAutoComplete from './CategoriaAutoComplete'

export default {
  props: ['lancamento'],
  data() {
    return {
      isEditando: false,
      backup: null
    }
  },
  watch: {
    lancamento(val) {
      this.backup = Object.assign({}, this.lancamento);
    },
    'lancamento.local'(atual, antigo) {
      this.validoParaSubmissao(atual, antigo);
    },
    'lancamento.categoria'(atual, antigo) {
      this.validoParaSubmissao(atual, antigo);
    }
  },
  methods: {
    validoParaSubmissao(atual, antigo) {
      if (this.lancamento && this.lancamento.id < 0)
        return;

      console.info(atual, antigo);
      if ('id' in atual && 'id' in antigo && (atual.id == antigo.id || antigo.id < 0))
        return;
      
      this.$store.dispatch(lancamentos.d.LANCAMENTO_EDICAO_SUBMIT, this.lancamento);
      this.isEditando = false;
    },
    css(valor) {
      return {
        'red--text': valor < 0,
        'blue--text text--darken-3': valor >= 0
      }
    },
    editando(campo) {
      this.isEditando = true;
      setTimeout(() => this.$refs[campo].focus(), 10);
    },
    blur(campo) {
      console.info('blur');
      this.isEditando = false;
    }
  },
  components: {
    LocalAutoComplete,
    CategoriaAutoComplete
  }
}
</script>

<style>
.inlineEdit .input-group--select .input-group__selections__comma{
  color: red;
  font-size: 13px;
}
</style>
