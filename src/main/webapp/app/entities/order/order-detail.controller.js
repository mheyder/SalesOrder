(function() {
    'use strict';

    angular
        .module('salesOrderApp')
        .controller('OrderDetailController', OrderDetailController);

    OrderDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Order', 'User', 'Coupon', 'Shipment', 'ShippingAddress', 'OrderItem'];

    function OrderDetailController($scope, $rootScope, $stateParams, previousState, entity, Order, User, Coupon, Shipment, ShippingAddress, OrderItem) {
        var vm = this;

        vm.order = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('salesOrderApp:orderUpdate', function(event, result) {
            vm.order = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
