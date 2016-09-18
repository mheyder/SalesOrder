(function() {
    'use strict';

    angular
        .module('salesOrderApp')
        .controller('ShipmentDeleteController',ShipmentDeleteController);

    ShipmentDeleteController.$inject = ['$uibModalInstance', 'entity', 'Shipment'];

    function ShipmentDeleteController($uibModalInstance, entity, Shipment) {
        var vm = this;

        vm.shipment = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Shipment.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
