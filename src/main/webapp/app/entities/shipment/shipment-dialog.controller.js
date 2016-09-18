(function() {
    'use strict';

    angular
        .module('salesOrderApp')
        .controller('ShipmentDialogController', ShipmentDialogController);

    ShipmentDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Shipment', 'Order'];

    function ShipmentDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Shipment, Order) {
        var vm = this;

        vm.shipment = entity;
        vm.clear = clear;
        vm.save = save;
        vm.orders = Order.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.shipment.id !== null) {
                Shipment.update(vm.shipment, onSaveSuccess, onSaveError);
            } else {
                Shipment.save(vm.shipment, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('salesOrderApp:shipmentUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
