<template>
  <v-container v-if="conta != null">
    <v-layout>
      <v-flex>
        <v-btn @click="dialogNovoLancamento = true">Novo lançamento</v-btn>
        <v-btn @click="excluirConta" color="error">Excluir conta</v-btn>
      </v-flex>
    </v-layout>
    <v-layout>
      <v-flex><v-btn @click="mesAnterior">Anterior</v-btn></v-flex>
      <v-flex><month-picker v-model="mesAtual" /></v-flex>
      <v-flex><v-btn @click="mesProximo">Próximo</v-btn></v-flex>
    </v-layout>
    <v-layout>
      <v-flex>
        <lancamento-list 
          :lancamentos="lancamentos[0]" 
          :saldos="lancamentos[1]" 
          @excluir="excluirLancamento"
          @alternaEfetiva="alternaEfetiva"  
          @subir="subirLancamento"
          @descer="descerLancamento"
          @editar="editarLancamento"
        />
      </v-flex>
    </v-layout>
    <v-dialog
      v-model="dialogNovoLancamento"
      width="600"
    >
      <v-card>
        <v-card-title class="headline">Novo lançamento na conta {{conta.nome}}</v-card-title>
        <v-card-text>
          <lancamento-form :idConta="conta.id" @salvo="dialogNovoLancamento = false" />
        </v-card-text>
      </v-card>
    </v-dialog>

    <v-dialog
      v-model="dialogEditarLancamento"
      width="600"
    >
      <v-card v-if="lancamentoEditar" :key="JSON.stringify(lancamentoEditar)">
        <v-card-title class="headline">Editando lançamento {{conta.nome}}</v-card-title>
        <v-card-text>
          <lancamento-form :idConta="conta.id" :lancamento="lancamentoEditar" @salvo="dialogEditarLancamento = false" />
        </v-card-text>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
import {mapState} from 'vuex'
import LancamentoForm from '../components/LancamentoForm'
import LancamentoList from '../components/LancamentoList'
import moment from 'moment'

export default {
  props: ['conta'],
  data() {
    return {
      dialogNovoLancamento: false,
      dialogEditarLancamento: false,
      mesAtual: moment(),
      lancamentoEditar: null
    }
  },
  methods: {
    excluirConta() {
      this.$confirm("Tem certeza disso?").then(val => {
        if (val) {
          this.$store.dispatch("contaExcluir", this.conta.id)
          this.$router.push({
            name: 'home'
          })
        }
      })
    },
    excluirLancamento(lancamento) {
      this.$confirm("Tem certeza disso? HÁ UM BUG EM QUE LANÇAMENTOS DE RECORRENCIA AINDA NÃO PODEM SER EXCLUIDOS").then(val => {
        if (val) {
          if (lancamento.idContaDestino)
            this.$store.dispatch("transferenciaExcluir", lancamento)
          else
            this.$store.dispatch("lancamentoExcluir", lancamento)
        }
      })
    },
    alternaEfetiva(lancamento) {
      lancamento.efetivada = !lancamento.efetivada
      if (lancamento.idContaDestino)
        this.$store.dispatch("transferenciaSalvar", lancamento)
      else
        this.$store.dispatch("lancamentoSalvar", lancamento)
    },
    subirLancamento(idLancamento) {
      this.$store.dispatch("lancamentoSubir", this.$store.getters.lancamentoId(idLancamento))
    },
    descerLancamento(idLancamento) {
      this.$store.dispatch("lancamentoDescer", this.$store.getters.lancamentoId(idLancamento))
    },
    mesAnterior() {
      this.mesAtual = moment(this.mesAtual).add(-1, 'month')
    },
    mesProximo() {
      this.mesAtual = moment(this.mesAtual).add(1, 'month')
    },
    editarLancamento(lancamento) {
      this.lancamentoEditar = lancamento
      this.dialogEditarLancamento = true
    }
  },
  computed: {
    lancamentos() {
      const [de, ate] = this.periodo
      
      return this.$store.getters.lancamentos([this.conta], de, ate)
    },
    periodo() {
      return [this.mesAtual, this.mesAtual.clone().endOf('month')]
    }

  },
  components: {
    LancamentoForm,
    LancamentoList
  }
}
</script>

<style>

</style>
