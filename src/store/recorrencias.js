import repo from './repo'
import moment from 'moment'

const descricaoLancamento = (recorrencia, ix) => {
  return  recorrencia.isParcelamento 
    ? recorrencia.descricao + ` (${ix}/${recorrencia.parcelaFim})`
    : recorrencia.descricao
}

const recorrencia = {
  valor: 0,
  frequencia: 'MENSAL',
  isIndefinidamente: false,
  dataFim: null,
  idConta: '',
  lancamentos: [
    {
      data: new Date(),
      idLancamento: ''
    }
  ]
}

const state = {
  byConta: {

  }
}

const mutations = {
  addRecorrencia(state, recorrencia) {
    state.byConta[recorrencia.idConta] = state.byConta[recorrencia.idConta] || []
    state.byConta[recorrencia.idConta].push(recorrencia)
  }
}

const periodos = {
  mensal: (periodo) => {
    const val = periodo.clone().add(1, 'month')
    
    return moment(val.format('YYYYMMDD'), 'YYYYMMDD')
  } 
}

const getters = {
  recorrenciasDaConta: (state) => (idConta, periodoAte) => {
    const recorrencias = state.byConta[idConta] || []
    
    return recorrencias.filter(r => 
      r.dataInicio.isSameOrBefore(periodoAte)
    )
  },
  lancamentosRecorrentesDaConta: (state, getters) => (idConta, periodoAte) => {
    const recorrencias = getters.recorrenciasDaConta(idConta, periodoAte)
    
    return recorrencias.map(r => {
      const lancamentosGerados = getters.lancamentosDaRecorrencia(r.id)

      const periodoAdicionar = periodos[r.frequencia]
      let dataAtual = periodoAdicionar(r.dataInicio)
      const lancamentos = []
      let ix = (r.parcelaInicio || 1) + 1
      while (dataAtual.isSameOrBefore(periodoAte) && (r.dataFim == null || dataAtual.isSameOrBefore(r.dataFim))) {
        const lancamentoExistente = lancamentosGerados.find(l => l.dataOriginal.isSame(dataAtual))
        
        if (!lancamentoExistente) {
          lancamentos.push({
            data: dataAtual,
            dataOriginal: dataAtual,
            descricao: descricaoLancamento(r, ix),
            efetivada: false,
            idConta: r.idConta,
            idRecorrencia: r.id,
            valor: r.valor
          })
        }

        ix += 1
        dataAtual = periodoAdicionar(dataAtual)
      }
      return lancamentos
    }).reduce((x, y) => [...x, ...y], [])

  }
}

const actions = {
  posLogin({commit}) {
    repo.hookAdded('/recorrencias', recorrencia => {
      recorrencia.dataInicio = moment(recorrencia.dataInicio)
      recorrencia.dataFim = recorrencia.dataFim != null || recorrencia.dataFim != undefined
        ? moment(recorrencia.dataFim)
        : null
      commit('addRecorrencia', recorrencia)
    })
  },
  recorrenciaSalvar({dispatch}, {recorrenciaBase, lancamentoBase}) {
    const recorrencia = {
      valor: lancamentoBase.valor,
      frequencia: 'mensal',
      isIndefinidamente: recorrenciaBase.isIndefinidamente,
      idConta: lancamentoBase.idConta,
      dataFim: recorrenciaBase.dataFim,
      dataInicio: lancamentoBase.data,
      descricao: lancamentoBase.descricao,
      parcelaInicio: recorrenciaBase.parcelaInicio,
      parcelaFim: recorrenciaBase.parcelaFim,
      isParcelamento: recorrenciaBase.parcelaInicio != null && recorrenciaBase.parcelaFim != null
    }

    if (recorrencia.isParcelamento) {
      const funcaoAdicao = periodos[recorrencia.frequencia]
      let dataFim = recorrencia.dataInicio
      let count = recorrencia.parcelaInicio
      while (count < recorrencia.parcelaFim) {
        dataFim = funcaoAdicao(dataFim)
        count += 1
      }
      recorrencia.dataFim = dataFim
    }

    const lancamento = {
      data: lancamentoBase.data,
      dataOriginal: lancamentoBase.data,
      idConta: lancamentoBase.idConta,
      descricao: descricaoLancamento(recorrencia, recorrencia.parcelaInicio),
      efetivada: lancamentoBase.efetivada,
      valor: lancamentoBase.valor
    }

    return new Promise((acc) => {
      repo.save('/recorrencias', recorrencia).then((idRecorrencia) => {
        lancamento.idRecorrencia = idRecorrencia
        dispatch('lancamentoSalvar', lancamento)
        acc()
      })
    })
  }
}

export default {
  state, 
  getters,
  actions,
  mutations
}