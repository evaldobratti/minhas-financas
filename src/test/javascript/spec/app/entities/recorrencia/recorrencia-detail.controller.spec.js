'use strict';

describe('Controller Tests', function() {

    describe('Recorrencia Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockRecorrencia, MockLancamento, MockConta, MockCategoria;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockRecorrencia = jasmine.createSpy('MockRecorrencia');
            MockLancamento = jasmine.createSpy('MockLancamento');
            MockConta = jasmine.createSpy('MockConta');
            MockCategoria = jasmine.createSpy('MockCategoria');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'Recorrencia': MockRecorrencia,
                'Lancamento': MockLancamento,
                'Conta': MockConta,
                'Categoria': MockCategoria
            };
            createController = function() {
                $injector.get('$controller')("RecorrenciaDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'minhasfinancasApp:recorrenciaUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
