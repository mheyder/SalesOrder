(function() {
    'use strict';

    angular
        .module('salesOrderApp')
        .controller('OrderItemDetailController', OrderItemDetailController);

    OrderItemDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'OrderItem', 'Product', 'Order'];

    function OrderItemDetailController($scope, $rootScope, $stateParams, previousState, entity, OrderItem, Product, Order) {
        var vm = this;

        vm.orderItem = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('salesOrderApp:orderItemUpdate', function(event, result) {
            vm.orderItem = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
