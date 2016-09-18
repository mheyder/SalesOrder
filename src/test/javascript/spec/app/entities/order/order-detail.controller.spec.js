'use strict';

describe('Controller Tests', function() {

    describe('Order Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockOrder, MockUser, MockCoupon, MockShipment, MockShippingAddress, MockOrderItem;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockOrder = jasmine.createSpy('MockOrder');
            MockUser = jasmine.createSpy('MockUser');
            MockCoupon = jasmine.createSpy('MockCoupon');
            MockShipment = jasmine.createSpy('MockShipment');
            MockShippingAddress = jasmine.createSpy('MockShippingAddress');
            MockOrderItem = jasmine.createSpy('MockOrderItem');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'Order': MockOrder,
                'User': MockUser,
                'Coupon': MockCoupon,
                'Shipment': MockShipment,
                'ShippingAddress': MockShippingAddress,
                'OrderItem': MockOrderItem
            };
            createController = function() {
                $injector.get('$controller')("OrderDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'salesOrderApp:orderUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
