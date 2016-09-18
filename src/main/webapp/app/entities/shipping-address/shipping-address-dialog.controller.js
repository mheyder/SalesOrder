(function() {
    'use strict';

    angular
        .module('salesOrderApp')
        .controller('ShippingAddressDialogController', ShippingAddressDialogController);

    ShippingAddressDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'ShippingAddress', 'User'];

    function ShippingAddressDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, ShippingAddress, User) {
        var vm = this;

        vm.shippingAddress = entity;
        vm.clear = clear;
        vm.save = save;
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.shippingAddress.id !== null) {
                ShippingAddress.update(vm.shippingAddress, onSaveSuccess, onSaveError);
            } else {
                ShippingAddress.save(vm.shippingAddress, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('salesOrderApp:shippingAddressUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
