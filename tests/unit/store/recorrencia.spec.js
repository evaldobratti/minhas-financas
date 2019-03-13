import recorrencias from '@/store/recorrencias'
import moment from 'moment'

jest.mock('@/store/repo')
import repo from '@/store/repo'

const ID_LANCAMENTO = 22
const JANEIRO_1 = moment("20110101", "YYYYMMDD")
const RECORRENCIA_BASE = {
  dataInicio: JANEIRO_1,
  dataFim: null,
  valor: -100,
  frequencia: 'mensal',
  idConta: 1,
  isIndefinidamente: false,
  descricao: 'Recorrencia',
  dataFim: undefined,
  isParcelamento: false,
  parcelaInicio: undefined,
  parcelaFim: undefined
}

describe('Recorrencia', () => {
  const recorrenciaNoPeriodo = {
    ...RECORRENCIA_BASE,
    id: 'recorrencia1',
    descricao: 'Recorrencia normal',
  }
  
  const recorrenciaNoPeriodoComDataFim = {
    ...RECORRENCIA_BASE, 
    id: 'recorrencia2',
    descricao: 'Recorrencia com dataFim',
    dataFim: moment('20110228', 'YYYYMMDD')
  }

  beforeEach(() => {
    repo.save.mockReset()
  })

  it('nova', async () => {
    const fn = jest.fn(() => new Promise((acc) => { acc()}))
    
    repo.save.mockImplementation(() => new Promise((acc) => { acc('recorrenciaID')})) 

    const a = await recorrencias.actions.recorrenciaSalvar({dispatch: fn}, {
      recorrenciaBase: {
        isIndefinidamente: false
      },
      lancamentoBase: {
        valor: -100,
        data: JANEIRO_1,
        idConta: 1,
        efetivada: false,
        descricao: 'Recorrencia'
      }
    })
    
    expect(fn.mock.calls[0]).toEqual(['lancamentoSalvar', {
      data: JANEIRO_1,
      dataOriginal: JANEIRO_1,
      descricao: 'Recorrencia',
      efetivada: false,
      idRecorrencia: 'recorrenciaID',
      idConta: 1,
      valor: -100
    }])

    expect(repo.save.mock.calls[0]).toEqual(['/recorrencias', RECORRENCIA_BASE])
  })

  it('deve calcular recorrencias do periodo', async() => {
    
    const recorrenciaForaDoPeriodoAntes = {...RECORRENCIA_BASE, dataInicio: moment('20101001', 'YYYYMMDD'), dataFim: moment('20101231', 'YYYYMMDD')}
    const recorrenciaForaDoPeriodoDepois = {...RECORRENCIA_BASE, dataInicio: moment('20121001', 'YYYYMMDD'), dataFim: moment('20121231', 'YYYYMMDD')}

    const list = recorrencias.getters.recorrenciasDaConta({
      byConta: {
        [1]: [
          recorrenciaNoPeriodo, recorrenciaNoPeriodoComDataFim, recorrenciaForaDoPeriodoAntes, recorrenciaForaDoPeriodoDepois
        ]
      }
    })(1, moment("20110301", "YYYYMMDD"))

    expect(list).toEqual(expect.arrayContaining([recorrenciaNoPeriodo, recorrenciaNoPeriodoComDataFim]))
  })

  it('deve calcular lancamento do periodo em recorrencia sem fim', async() => {
    const lancamentos = recorrencias.getters.lancamentosRecorrentesDaConta(null, {
      recorrenciasDaConta: () => [recorrenciaNoPeriodo],
      lancamentosDaRecorrencia: () => []
    })(1, moment("20110301", "YYYYMMDD"))

    expect(lancamentos).toEqual([
      {
        data: moment("20110201", "YYYYMMDD"),
        dataOriginal: moment("20110201", "YYYYMMDD"),
        descricao: "Recorrencia normal",
        efetivada: false,
        idConta: 1,
        idRecorrencia: "recorrencia1",
        valor: -100,
      },
      {
        data: moment("20110301", "YYYYMMDD"),
        dataOriginal: moment("20110301", "YYYYMMDD"),
        descricao: "Recorrencia normal",
        efetivada: false,
        idConta: 1,
        idRecorrencia: "recorrencia1",
        valor: -100
      },
    ])
  })

  it('deve calcular lancamento do periodo em recorrencia com fim', async() => {
    const lancamentos = recorrencias.getters.lancamentosRecorrentesDaConta(null, {
      recorrenciasDaConta: () => [recorrenciaNoPeriodoComDataFim],
      lancamentosDaRecorrencia: () => []
    })(1, moment("20110501", "YYYYMMDD"))

    expect(lancamentos).toEqual([
      {
        data: moment("20110201", "YYYYMMDD"),
        dataOriginal: moment("20110201", "YYYYMMDD"),
        descricao: "Recorrencia com dataFim",
        efetivada: false,
        idConta: 1,
        idRecorrencia: "recorrencia2",
        valor: -100,
      }
    ])
  })

  
  it('deve calcular lancamento do periodo com multiplas recorrencias', async() => {
    const lancamentos = recorrencias.getters.lancamentosRecorrentesDaConta(null, {
      recorrenciasDaConta: () => [recorrenciaNoPeriodo, recorrenciaNoPeriodoComDataFim],
      lancamentosDaRecorrencia: () => []
    })(1, moment("20110501", "YYYYMMDD"))

    expect(lancamentos).toEqual([
      {
        data: moment("20110201", "YYYYMMDD"),
        dataOriginal: moment("20110201", "YYYYMMDD"),
        descricao: "Recorrencia normal",
        efetivada: false,
        idConta: 1,
        idRecorrencia: "recorrencia1",
        valor: -100,
      },
      {
        data: moment("20110301", "YYYYMMDD"),
        dataOriginal: moment("20110301", "YYYYMMDD"),
        descricao: "Recorrencia normal",
        efetivada: false,
        idConta: 1,
        idRecorrencia: "recorrencia1",
        valor: -100
      },
      {
        data: moment("20110401", "YYYYMMDD"),
        dataOriginal: moment("20110401", "YYYYMMDD"),
        descricao: "Recorrencia normal",
        efetivada: false,
        idConta: 1,
        idRecorrencia: "recorrencia1",
        valor: -100
      },
      {
        data: moment("20110501", "YYYYMMDD"),
        dataOriginal: moment("20110501", "YYYYMMDD"),
        descricao: "Recorrencia normal",
        efetivada: false,
        idConta: 1,
        idRecorrencia: "recorrencia1",
        valor: -100
      },
      {
        data: moment("20110201", "YYYYMMDD"),
        dataOriginal: moment("20110201", "YYYYMMDD"),
        descricao: "Recorrencia com dataFim",
        efetivada: false,
        idConta: 1,
        idRecorrencia: "recorrencia2",
        valor: -100,
      }
    ])
  })

  it('nao deve criar lancamento se ja existir um para aquela data', async() => {
    const lancamentos = recorrencias.getters.lancamentosRecorrentesDaConta(null, {
      recorrenciasDaConta: () => [recorrenciaNoPeriodoComDataFim],
      lancamentosDaRecorrencia: () => [{
        dataOriginal: moment('20110201', 'YYYYMMDD')
      }]
    })(1, moment("20110501", "YYYYMMDD"))

    expect(lancamentos.length).toBe(0)
  })

  it('novo parcelamento', async () => {
    const fn = jest.fn(() => new Promise((acc) => { acc()}))
    
    repo.save.mockImplementation(() => new Promise((acc) => { acc('recorrenciaID')})) 

    const a = await recorrencias.actions.recorrenciaSalvar({dispatch: fn}, {
      recorrenciaBase: {
        isIndefinidamente: false,
        parcelaInicio: 1,
        parcelaFim: 3
      },
      lancamentoBase: {
        valor: -100,
        data: JANEIRO_1,
        idConta: 1,
        efetivada: false,
        descricao: 'Recorrencia'
      }
    })
    
    expect(fn.mock.calls[0]).toEqual(['lancamentoSalvar', {
      data: JANEIRO_1,
      dataOriginal: JANEIRO_1,
      descricao: 'Recorrencia (1/3)',
      efetivada: false,
      idRecorrencia: 'recorrenciaID',
      idConta: 1,
      valor: -100
    }])

    expect(repo.save.mock.calls[0]).toEqual(['/recorrencias', {
      dataFim: moment('20110301', 'YYYYMMDD'), 
      dataInicio: JANEIRO_1, 
      descricao: "Recorrencia", 
      frequencia: "mensal", 
      idConta: 1, 
      isIndefinidamente: false, 
      isParcelamento: true, 
      parcelaFim: 3, 
      parcelaInicio: 1, 
      valor: -100
    }])
  })

  it('faz projecao um lancamento de parcelamento', async () => {
    const parcelamento = {
      dataFim: moment('20110301', 'YYYYMMDD'), 
      dataInicio: JANEIRO_1, 
      descricao: "Recorrencia", 
      frequencia: "mensal", 
      idConta: 1, 
      isIndefinidamente: false, 
      isParcelamento: true, 
      parcelaFim: 3, 
      parcelaInicio: 1, 
      valor: -100,
      id: 'parcelamentoId'
    }

    const lancamentos = recorrencias.getters.lancamentosRecorrentesDaConta(null, {
      recorrenciasDaConta: () => [parcelamento],
      lancamentosDaRecorrencia: () => []
    })(1, moment("20110201", "YYYYMMDD"))
    
    expect(lancamentos).toEqual([{
      data: moment("20110201", "YYYYMMDD"),
      dataOriginal: moment("20110201", "YYYYMMDD"),
      descricao: "Recorrencia (2/3)",
      efetivada: false,
      idConta: 1,
      idRecorrencia: "parcelamentoId",
      valor: -100,
    }])
  })

  it('faz projecao de dois lancamentos de parcelamento', async () => {
    const parcelamento = {
      dataFim: moment('20110301', 'YYYYMMDD'), 
      dataInicio: JANEIRO_1, 
      descricao: "Recorrencia", 
      frequencia: "mensal", 
      idConta: 1, 
      isIndefinidamente: false, 
      isParcelamento: true, 
      parcelaFim: 3, 
      parcelaInicio: 1, 
      valor: -100,
      id: 'parcelamentoId'
    }

    const lancamentos = recorrencias.getters.lancamentosRecorrentesDaConta(null, {
      recorrenciasDaConta: () => [parcelamento],
      lancamentosDaRecorrencia: () => []
    })(1, moment("20110401", "YYYYMMDD"))
    
    expect(lancamentos).toEqual(expect.arrayContaining([{
      data: moment("20110201", "YYYYMMDD"),
      dataOriginal: moment("20110201", "YYYYMMDD"),
      descricao: "Recorrencia (2/3)",
      efetivada: false,
      idConta: 1,
      idRecorrencia: "parcelamentoId",
      valor: -100,
    }, {
      data: moment("20110301", "YYYYMMDD"),
      dataOriginal: moment("20110301", "YYYYMMDD"),
      descricao: "Recorrencia (3/3)",
      efetivada: false,
      idConta: 1,
      idRecorrencia: "parcelamentoId",
      valor: -100,
    }]))
  })


})