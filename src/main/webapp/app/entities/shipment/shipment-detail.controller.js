(function() {
    'use strict';

    angular
        .module('salesOrderApp')
        .controller('ShipmentDetailController', ShipmentDetailController);

    ShipmentDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Shipment', 'Order'];

    function ShipmentDetailController($scope, $rootScope, $stateParams, previousState, entity, Shipment, Order) {
        var vm = this;

        vm.shipment = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('salesOrderApp:shipmentUpdate', function(event, result) {
            vm.shipment = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
