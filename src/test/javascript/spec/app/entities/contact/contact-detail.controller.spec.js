'use strict';

describe('Controller Tests', function() {

    describe('Contact Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockContact, MockPhone, MockAddress, MockEmailAddress;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockContact = jasmine.createSpy('MockContact');
            MockPhone = jasmine.createSpy('MockPhone');
            MockAddress = jasmine.createSpy('MockAddress');
            MockEmailAddress = jasmine.createSpy('MockEmailAddress');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Contact': MockContact,
                'Phone': MockPhone,
                'Address': MockAddress,
                'EmailAddress': MockEmailAddress
            };
            createController = function() {
                $injector.get('$controller')("ContactDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'contactsApp:contactUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
