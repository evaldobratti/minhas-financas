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
      width="400"
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
      width="400"
    >
      <v-card v-if="lancamentoEditar" :key="lancamentoEditar.id">
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
  created() {
    this.validaExistenciaConta()
  },
  data() {
    return {
      dialogNovoLancamento: false,
      dialogEditarLancamento: false,
      mesAtual: moment(),
      lancamentoEditar: null
    }
  },
  methods: {
    validaExistenciaConta() {

    },
    excluirConta() {
      if (confirm("Tem certeza disso?")) {
        this.$store.dispatch("contaExcluir", this.conta.id)
        this.$router.push({
          name: 'home'
        })
      }
      
    },
    excluirLancamento(idLancamento) {
      if (confirm("Tem certeza disso?"))
        this.$store.dispatch("lancamentoExcluir", this.$store.getters.lancamentoId(idLancamento))
    },
    alternaEfetiva(idLancamento) {
      this.$store.dispatch("lancamentoSalvar", this.$store.getters.lancamentoId(idLancamento))
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
    editarLancamento(idLancamento) {
      this.lancamentoEditar = this.$store.getters.lancamentoId(idLancamento)
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
