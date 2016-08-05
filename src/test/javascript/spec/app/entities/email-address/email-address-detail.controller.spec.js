'use strict';

describe('Controller Tests', function() {

    describe('EmailAddress Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockEmailAddress, MockTypeValue, MockContact;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockEmailAddress = jasmine.createSpy('MockEmailAddress');
            MockTypeValue = jasmine.createSpy('MockTypeValue');
            MockContact = jasmine.createSpy('MockContact');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'EmailAddress': MockEmailAddress,
                'TypeValue': MockTypeValue,
                'Contact': MockContact
            };
            createController = function() {
                $injector.get('$controller')("EmailAddressDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'contactsApp:emailAddressUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
