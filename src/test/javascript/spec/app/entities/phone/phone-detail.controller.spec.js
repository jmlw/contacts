'use strict';

describe('Controller Tests', function() {

    describe('Phone Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPhone, MockTypeValue, MockContact;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPhone = jasmine.createSpy('MockPhone');
            MockTypeValue = jasmine.createSpy('MockTypeValue');
            MockContact = jasmine.createSpy('MockContact');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Phone': MockPhone,
                'TypeValue': MockTypeValue,
                'Contact': MockContact
            };
            createController = function() {
                $injector.get('$controller')("PhoneDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'contactsApp:phoneUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
