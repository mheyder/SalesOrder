(function() {
    'use strict';

    angular
        .module('salesOrderApp')
        .controller('ShippingAddressDeleteController',ShippingAddressDeleteController);

    ShippingAddressDeleteController.$inject = ['$uibModalInstance', 'entity', 'ShippingAddress'];

    function ShippingAddressDeleteController($uibModalInstance, entity, ShippingAddress) {
        var vm = this;

        vm.shippingAddress = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            ShippingAddress.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
