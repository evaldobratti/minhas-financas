<template>
<tr class="lancamentoLinha" 
  :style="{ color: isProjecaoNaoPersistida() ? 'gray' : 'black'}" 
  :title="isProjecaoNaoPersistida() ? 'Este lançamento é uma projeção de uma recorrência que ainda não foi salvo. Quando ele for salvo o texto ficará preto.' : ''">
  <td xs3>
    {{ lancamento.data | date }}
  </td>
  <td>
    <span @click.stop="editando('descricao')">
      
      {{ lancamento.local + getComplemento() }} 
      <v-icon v-if="isRecorrente" style="float: right">refresh</v-icon>  
    </span>
  </td>
    <td>
      <span @click.stop="editando('categoria')" v-if="lancamento.idCategoria != null">{{ getCategoria(lancamento.idCategoria).nome }}</span>
    </td>
    <td class="text-xs-right" :class="css(lancamento.valor)">
        <span @click.stop="editando('valor')">{{ lancamento.valor | currency }}</span>
    </td>
    <td class="text-xs-right" :class="css(lancamento.saldoDiario)">{{ lancamento.saldoDiario | currency }}</td>
    <td>
      <v-checkbox style="width: 40px" v-model="lancamento.efetivada" v-if="lancamento.tempId || lancamento.id"></v-checkbox>
    </td>
    <td>
      <span class="ordenacao" v-if="!isProjecaoNaoPersistida()">
        <v-btn flat icon small @click="sobe()" style="margin: 0" v-if="podeSubir && lancamento.id != null">
          <v-icon>keyboard_arrow_up</v-icon>
        </v-btn>
        <v-btn flat icon small @click="desce()" style="margin: 0" v-if="podeDescer && lancamento.id != null">
          <v-icon>keyboard_arrow_down</v-icon>
        </v-btn>
      </span>
    </td>
    <td>
      
        <v-menu lazy style="width: 40px"  bottom right>
        <v-btn icon slot="activator">
            <v-icon>more_vert</v-icon>
        </v-btn>
        <v-list>
            <v-list-tile @click="$emit('novaRecorrencia', lancamento)" :disabled="!(lancamento.motivo == null || lancamento.motivo['@class'].endsWith('RecorrenciaLancamentoGerado'))">
              <v-list-tile-title>
                  <v-btn color="primary" fab small dark class="small-fab-btn">
                  <v-icon>refresh</v-icon>
                  </v-btn>
                  Repetição
              </v-list-tile-title>
            </v-list-tile>
            <v-list-tile @click="$emit('deleteLancamento', lancamento)">
              <v-list-tile-title>
                  <v-btn fab small dark class="small-fab-btn red">
                  <v-icon>remove</v-icon>
                  </v-btn>
                  Apagar
              </v-list-tile-title>
            </v-list-tile>
            <v-list-tile @click="$emit('efetiva', lancamento)" :disabled="lancamento.id != null" >
              <v-list-tile-title>
                  <v-btn fab small dark class="small-fab-btn green">
                  <v-icon>add</v-icon>
                  </v-btn>
                  Efetivar
              </v-list-tile-title>
            </v-list-tile>
            <v-list-tile @click="$emit('trocaConta', lancamento)">
              <v-list-tile-title>
                <v-btn fab small dark class="small-fab-btn green">
                 <v-icon>swap_horiz</v-icon>
                </v-btn>
                Trocar de conta
              </v-list-tile-title>
            </v-list-tile>
            <v-list-tile @click="$emit('transferencia', lancamento)">
              <v-list-tile-title>
                <v-btn fab small dark class="small-fab-btn green">
                 <v-icon>swap_horiz</v-icon>
                </v-btn>
                Transferencia
              </v-list-tile-title>
            </v-list-tile>
        </v-list>
        </v-menu>
    </td>
    <v-dialog 
      v-model="isEditando" 
      lazy 
      max-width="80%">
      <v-card>
        <LancamentoForm 
          ref="form" 
          :id-conta="lancamento.idConta" 
          :lancamento="lancamento"
          @submetido="isEditando=false" />
      </v-card>
    </v-dialog>
  </tr>
</template>

<script>
import { lancamentos } from '../../store/lancamento';
import mapGetSet from '../../store/mapGetSet';
import LocalAutoComplete from './LocalAutoComplete';
import CategoriaAutoComplete from './CategoriaAutoComplete'
import LancamentoForm from './LancamentoForm';

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
      this.backup = Object.assign(Object.create(this.lancamento), this.lancamento);
    },
    'lancamento.efetivada'() {
      this.$store.dispatch(lancamentos.d.LANCAMENTO_SUBMIT, this.lancamento);
    }
  },
  computed: {
    podeSubir() {
      const lancamentos = this.lancamentosDia();
      return lancamentos[0] != this.lancamento;
    },
    podeDescer() {
      const lancamentos = this.lancamentosDia();
      return lancamentos[lancamentos.length - 1] != this.lancamento;
    },
    isRecorrente() {
      return this.$store.getters.isRecorrente(this.lancamento);
    }
  },
  methods: {
    lancamentosDia() {
      return this.$store.getters.lancamentosDia(this.lancamento.idConta, this.lancamento.data);
    },
    css(valor) {
      return {
        'red--text': valor < 0,
        'blue--text text--darken-3': valor >= 0
      }
    },
    getComplemento() {
      return this.$store.getters.getComplemento(this.lancamento);
    },
    editando(campo) {
      this.isEditando = true;
    },
    getLocal(idLocal) {
      return this.$store.getters.getLocal(idLocal);
    },
    getCategoria(idCategoria) {
      return this.$store.getters.getCategoria(idCategoria);
    },
    sobe() {
      this.$store.dispatch(lancamentos.d.SOBE_LANCAMENTO, this.lancamento);
    },
    desce() {
      this.$store.dispatch(lancamentos.d.DESCE_LANCAMENTO, this.lancamento);
    },
    isProjecaoNaoPersistida() {
      return this.isRecorrente && !this.lancamento.id;
    }
  },
  components: {
    LocalAutoComplete,
    CategoriaAutoComplete,
    LancamentoForm
  }
}
</script>

<style>
.inlineEdit .input-group--select .input-group__selections__comma{
  color: red;
  font-size: 13px;
}

.inlineEdit .input-group--select__autocomplete {
  font-size: 13px;
}

.small-btn-icon {
  margin: 0;
}

.small-btn-icon .btn__content {
  width: 10px;
}

.lancamentoLinha .ordenacao {
  opacity: 0;
}

.lancamentoLinha:hover .ordenacao {
  opacity: 1;
}
</style>
